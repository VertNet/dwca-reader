/***************************************************************************
 * Copyright 2010 Global Biodiversity Information Facility Secretariat
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ***************************************************************************/

package org.gbif.dwc.record;

import org.gbif.dwc.terms.Term;
import org.gbif.dwc.text.ArchiveField;
import org.gbif.dwc.text.ArchiveFile;
import org.gbif.file.CSVReader;
import org.gbif.utils.file.ClosableIterator;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordIterator implements ClosableIterator<Record> {

  private static final Logger LOG = LoggerFactory.getLogger(RecordIterator.class);
  private ClosableIterator<String[]> closable;
  private final PeekingIterator<String[]> iter;
  private int lineCount = 0;
  // needed to create RecordImpl
  private final ArchiveField id;
  private final Map<Term, ArchiveField> fields;
  private final String rowType;
  private final boolean replaceNulls;

  public RecordIterator(ClosableIterator<String[]> recordSource, ArchiveField id, Map<Term, ArchiveField> fields,
    String rowType, boolean replaceNulls) {
    this.id = id;
    this.fields = fields;
    this.rowType = rowType;
    this.replaceNulls = replaceNulls;
    closable = recordSource;
    if (closable == null) {
      Iterator<String[]> empty = Iterators.emptyIterator();
      iter = Iterators.peekingIterator(empty);
    } else {
      iter = Iterators.peekingIterator(closable);
    }
  }

  /**
   * @param replaceNulls if true record values will have literal nulls replaced with NULL.
   */
  public static RecordIterator build(ArchiveFile source, boolean replaceNulls) {
    try {
      CSVReader csvr = CSVReader.build(source);
      return new RecordIterator(csvr, source.getId(), source.getFields(), source.getRowType(), replaceNulls);
    } catch (IOException e) {
      LOG.error("Can't open archive file " + source + " for building a record iterator", e);
    }
    return null;
  }

  public void close() {
    closable.close();
  }

  public boolean hasNext() {
    return iter.hasNext() && iter.peek() != null;
  }

  public Record next() {
    lineCount++;
    RecordImpl record = null;
    try {
      // update record with cached row
      record = new RecordImpl(id, fields, rowType, replaceNulls);
      String[] row = iter.next();
      while (row.length == 0) {
        // ignore rows without a single column
        row = iter.next();
      }
      record.setRow(row);
    } catch (Exception e) {
      LOG.warn("Bad row somewhere around line: {}", lineCount, e);

    }
    return record;
  }

  public void remove() {
    throw new UnsupportedOperationException("Cannot remove a row from an archive file");
  }

}
