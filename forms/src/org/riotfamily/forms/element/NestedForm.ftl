<div id="${form.id}" class="nestedForm<#if !form.required> optional</#if> ${form.property?default('unbound')}">
<#if !form.required>
	<div class="<#if form.present>present<#else>not-present</#if>">
  		${toggleButton.render()}
	</div>	
</#if>
<#if form.required || form.present>
	<div class="indent elements">
		<#list elements.elements as element>
	    	<div class="item<#if element.styleClass?exists> ${element.styleClass}-element</#if>">
	    		<div class="label">
		    		<label for="${element.id}" class="field<#if element.form.errors.hasErrors(element)> error</#if>">
						${element.label?if_exists}<#if element.required>* </#if>
						<#if element.hint?exists>
							<span class="hint-trigger" onclick="toggleHint('${element.id}-hint')"></span>
						</#if>
					</label> 
				</div>
	    		<div class="element">
	    			<#if element.hint?exists>
						<div id="${element.id}-hint" class="hint">${element.hint}</div>
					</#if>
	    			${element.render()} ${element.form.errors.renderErrors(element)}
    			</div>
	    	</div>
		</#list>			
	</div>
</#if>
</div>