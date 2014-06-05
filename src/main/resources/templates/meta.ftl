<#macro escapeBackslash value>${value?replace("\n", "\\n")?replace("\r", "\\r")?replace("\t", "\\t")?replace("\f", "\\f")?xml}</#macro>
<#escape x as x?xml>
<archive xmlns="http://rs.tdwg.org/dwc/text/"<#if metadataLocation??> metadata="${metadataLocation}"</#if>>
  <core encoding="${core.encoding}" fieldsTerminatedBy="<@escapeBackslash value=core.fieldsTerminatedBy!"" />" linesTerminatedBy="<@escapeBackslash value=core.linesTerminatedBy!"" />" fieldsEnclosedBy="<@escapeBackslash value=core.fieldsEnclosedBy!"" />" ignoreHeaderLines="${core.ignoreHeaderLines!0}" rowType="${core.rowType}">
    <files>
     <#list core.locations as l>
      <location>${l}</location>
     </#list>
    </files>
    <id index="${core.id.index}" />
   <#list core.fieldsSorted as t>
    <field<#if t.index??> index="${t.index}"</#if><#if t.defaultValue?has_content> default="${t.defaultValue}"</#if> term="${t.term.qualifiedName()}"/>
   </#list>
  </core>
 <#list extensions as ext>
  <extension encoding="${ext.encoding}" fieldsTerminatedBy="<@escapeBackslash value=ext.fieldsTerminatedBy!"" />" linesTerminatedBy="<@escapeBackslash value=ext.linesTerminatedBy!"" />" fieldsEnclosedBy="<@escapeBackslash value=ext.fieldsEnclosedBy!"" />" ignoreHeaderLines="${ext.ignoreHeaderLines!0}" rowType="${ext.rowType}">
    <files>
     <#list ext.locations as l>
      <location>${l}</location>
     </#list>
    </files>
    <coreid index="${ext.id.index}" />
   <#list ext.fieldsSorted as t>
    <field<#if t.index??> index="${t.index}"</#if><#if t.defaultValue?has_content> default="${t.defaultValue}"</#if> term="${t.term.qualifiedName()}"/>
   </#list>
  </extension>
 </#list>
</archive>
</#escape>