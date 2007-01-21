riot.InplaceEditor = Class.create();
riot.InplaceEditor.prototype = {

	initialize: function(element, component, options) {
		this.element = element;
		this.component = component;
		this.key = element.getAttribute('riot:key');
		this.enabled = false;
		this.onclickHandler = this.onclick.bindAsEventListener(this);
		this.oninit(options);
	},
	
	/* Subclasses may override this method to perform initalization upon creation */
	oninit: function(options) {
		this.options = options || {};
	},
	
	/* Enables or disables the editor by adding (or removing) an onclick listener */
	setEnabled: function(enabled) {
		this.enabled = enabled;
		if (enabled) {
			this.originalOnlickHandler = this.element.onclick;
			this.element.onclick = this.onclickHandler;
			Element.addClassName(this.element, 'riot-editable-text');
		}
		else {
			if (riot.activeEditor == this) {
				this.close();
			}
			this.element.onclick = this.originalOnclickHandler;
			Element.removeClassName(this.element, 'riot-editable-text');
		}
	},
	
	/* Handler that is invoked when an enabled editor is clicked */
	onclick: function(ev) {
		Event.stop(ev);
		riot.toolbar.selectedComponent = this.component;
		riot.activeEditor = this;
		this.edit();
	},
	
	/* Acquires the current text and invokes setText() */
	edit: function() {
		this.setText(this.element.innerHTML);
	},

	/* Stores the given text as property and invokes showEditor() */	
	setText: function(text) {
		this.text = text;
		this.showEditor();
	},
	
	/* Subclasses must implement this method to show a widget that edits 
	 * the current text. 
	 */
	showEditor: function() {
	},
	
	/* Subclasses must implement this method to return the edited text. */
	getText: function() {
		return null;
	},
			
	save: function() {
		var text = this.getText();
		if (this.text != text) {
			ComponentEditor.updateText(this.component.componentList.controllerId,
					this.component.id, this.key, text, this.onupdate.bind(this));
					
			riot.toolbar.setDirty(this.component.componentList, true);
		}
		this.onsave(text);
	},
	
	/* Subclasses may override this method ... */
	onsave: function(text) {
	},
	
	/* Callback that is invoked after the text as been sucessfully submitted. */
	onupdate: function(html) {
	},
		
	/* This method is invoked when the active editor is disabled (either by
	 * enabling another editor or by switching to another tool).
	 * The default behaviour is to save all changes. Subclasses that provide
	 * an explicit save button (like the TextileEditor) may override this 
	 * method.
	 */
	close: function() {
		this.save();
	}
}

riot.InplaceTextEditor = riot.InplaceEditor.extend({

	oninit: function(options) {
		this.options = options || {};
		Element.makePositioned(this.element);
		this.input = document.createElement(this.options.multiline 
				? 'textarea' : 'input');
				
		if (this.options.multiline) this.input.wrap = 'off';
		
		Element.setStyle(this.input, {
			position: 'absolute', overflow: 'hidden',
			top: 0,	left: 0, border: 0, padding: 0, margin: 0,
			backgroundColor: 'transparent'
		});
		
		this.input.onkeyup = this.updateElement.bindAsEventListener(this);
		this.input.onblur = this.save.bindAsEventListener(this);
	
		Styles.clone(this.element, this.input, [
			'font-size', 'font-weight', 'font-family', 'font-style', 
			'color', 'background-color', 'text-align', 'text-decoration', 
			'letter-spacing', 'padding-left', 'padding-top']);
		
		if (this.options.multiline) {
			Styles.clone(this.element, this.input, ['line-height']);
		}
		
		this.input.style.boxSizing = this.input.style.MozBoxSizing = 'border-box';
		
		Element.hide(this.input);
		RElement.insertAfter(this.input, this.element);
	},
		
	edit: function() {
		this.setText(this.element.innerHTML.strip()
			.replace(/\s+/g, ' ')
			.replace(/<br[^>]*>/gi, '\n')
			.stripTags()
			.replace(/&amp;/g, '&')
			.replace(/&lt;/g, '<')
			.replace(/&gt;/g, '>')
		);
	},
		
	showEditor: function() {
		this.paddingLeft = 0;
		this.paddingTop = 0;
		if (browserInfo.ie || browserInfo.opera) { 
			var cm = document['compatMode'];
			if (cm != 'BackCompat' && cm != 'QuirksMode') {
				this.paddingLeft = parseInt(Element.getStyle(this.element, 'padding-left'));
				this.paddingTop = parseInt(Element.getStyle(this.element, 'padding-top'));
			}
		}
		Position.clone(this.element, this.input, {
			setWidth: false,
			setHeight: false
		});
		this.resize();
		RElement.makeInvisible(this.element);
		Element.show(this.input);
		this.input.focus();
		this.input.value = this.text;
	},
	
	getText: function() {
		return this.input.value
			.replace(/</g, '&lt;')
			.replace(/>/g, '&gt;')
			.replace(/&/g, '&amp;')
			.replace(/\n/g, '<br />');
	},
		
	onsave: function(text) {
		this.element.innerHTML = text;
		Element.hide(this.input);
		RElement.makeVisible(this.element);
	},

	updateElement: function() {
		this.element.innerHTML = this.getText().replace(/<br[^>]*>/gi, '<br />&nbsp;');
		this.resize();
	},

	resize: function() {
		this.input.style.width  = (this.element.offsetWidth - this.paddingLeft) + 'px';
    	this.input.style.height = (this.element.offsetHeight - this.paddingTop) + 'px';
	}
});


riot.PopupTextEditor = riot.InplaceEditor.extend({

	edit: function() {
		ComponentEditor.getText(this.component.id, this.key, 
			this.setText.bind(this));
	},
		
	showEditor: function() {
		this.popup = new riot.TextareaPopup(this);
		this.popup.open();
	},
	
	close: function() {
		this.popup.close();
		riot.activeEditor = null;
	},
	
	getText: function() {
		return this.popup.getText();
	},
	
	onsave: function() {
		this.close();
	},
	
	onupdate: function(html) {
		this.component.setHtml(html);
	}
		
});

riot.RichtextEditor = riot.PopupTextEditor.extend({
	showEditor: function() {
		Resources.loadScriptSequence([
			{src: 'tiny_mce/tiny_mce_src.js', test: 'tinyMCE'},
			{src: 'tiny_mce/strict_mode_fix.js', test: 'tinyMCE.strictModeFixed'}
		]);
		Resources.waitFor('tinyMCE.strictModeFixed', this.openPopup.bind(this));
	},
	
	openPopup: function() {
		var p = this.popup = new riot.TinyMCEPopup(this);
		Resources.waitFor(function() { return p.ready }, p.open.bind(p));
	},
	
	save: function() {
		if (this.options.split) {
			var text = this.getText();
			if (this.text != text) {
				var chunks = [];
				var n = RBuilder.node('div');
				n.innerHTML = text;
				$A(n.childNodes).each(function(c) {
					if (c.nodeType == 1) {
						chunks.push('<' + c.nodeName + '>' + c.innerHTML 
								+ '</' + c.nodeName + '>');
					}
					else if (c.nodeType == 3) {
						chunks.push('<p>' + c.nodeValue + '</p>');
					}
				});
				if (chunks.length == 0) {
					chunks.push('<p></p>');
				}
				ComponentEditor.updateTextChunks(
						this.component.componentList.controllerId,
						this.component.id, this.key, chunks, 
						this.component.onupdate.bind(this.component));
			}
			this.onsave(text);
		}
		else {
			this.SUPER();
		}
	}

});

riot.Popup = Class.create();
riot.Popup.prototype = {
	initialize: function(title, content, ok, help) {
		this.ok = ok;
		this.overlay = RBuilder.node('div', {id: 'riot-overlay', style: {display: 'none'}});
		this.div = RBuilder.node('div', {id: 'riot-popup', style: {position: 'absolute'}},
			help ? RBuilder.node('div', {className: 'riot-help-button', onclick: help}) : null, 
			this.closeButton = RBuilder.node('div', {className: 'riot-close-button', onclick: this.close.bind(this)}), 
			RBuilder.node('div', {className: 'headline'}, title), 
			this.content = content,
			this.okButton = RBuilder.node('div', {className: 'button-ok', onclick: ok}, 'Ok')
		);
		RElement.makeInvisible(this.div);
		document.body.appendChild(this.overlay);
		document.body.appendChild(this.div);
	},
	
	hideElements: function(name) {
		$A(document.getElementsByTagName(name)).each(function (e) {
			if (!Element.childOf(e, this.div)) {
				RElement.makeInvisible(e);
				e.hidden = true;
			}
		});
	},
	
	showElements: function(name) {
		$A(document.getElementsByTagName(name)).each(function (e) {
			if (e.hidden) {
				RElement.makeVisible(e);
				e.hidden = false;
			}
		});
	},
		
	open: function() {
		if (browserInfo.ie) this.hideElements('select');
		this.hideElements('object');
		this.hideElements('embed');

		var top = Math.round(Viewport.getInnerHeight() / 2 - this.div.clientHeight / 2);
		var left = Math.round(Viewport.getInnerWidth() / 2 - this.div.clientWidth / 2);
		
		Element.hide(this.div);
		this.div.style.position = '';
		if (Element.getStyle(this.div, 'position') != 'fixed') {
			top += Viewport.getScrollTop();
			left += Viewport.getScrollLeft();
		}
		this.div.style.top = top + 'px';
		this.div.style.left = left + 'px';

		Element.show(this.overlay);
		RElement.makeVisible(this.div);
		Element.show(this.div);
		this.isOpen = true;
	},
		
	close: function() {
		if (browserInfo.ie) this.showElements('select');
		this.showElements('object');
		this.showElements('embed');
		Element.remove(this.div);
		Element.remove(this.overlay);
		this.isOpen = false;
	}
}

riot.TextareaPopup = riot.Popup.extend({

	initialize: function(editor) {
		this.textarea = RBuilder.node('textarea', {value: editor.text || ''}),
		this.SUPER('${editor-popup.title}', this.textarea, editor.save.bind(editor), editor.help);
	},
	
	setText: function(text) {
		this.textarea.value = text;
		this.textarea.focus();
	},
	
	getText: function() {
		return this.textarea.value;
	}
	
});

riot.TinyMCEPopup = riot.TextareaPopup.extend({
	initialize: function(editor) {
		this.SUPER(editor);
		if (this.textarea.value == '') {
			this.textarea.value = '<p>&nbsp;</p>';
		}
		RElement.makeInvisible(this.textarea);
		this.textarea.style.position = 'absolute';
		riot.initTinyMCE();
		Resources.waitFor('tinyMCELang["lang_theme_block"]', 
				this.addMCEControl.bind(this));
	},
	
	addMCEControl: function() {
		tinyMCE.addMCEControl(this.textarea);
		this.ready = true;
	},

	close: function() {
		tinyMCE.instances = tinyMCE.instances.without(tinyMCE.selectedInstance);
		tinyMCE.selectedInstance = null;
		this.SUPER();
	},
	
	setText: function(text) {
		tinyMCE.setContent(text);
	},
	
	getText: function() {
		return tinyMCE.getContent();
	}
	
});

riot.stylesheetMaker = {

	properties: {
		'*': ['font-family', 'font-size', 'font-weight', 'font-style',
			'line-height', 'text-decoration', 'color', 'background-color',
			'margin-top', 'margin-right', 'margin-bottom', 'margin-left',
			'padding-top', 'padding-right', 'padding-bottom', 'padding-left'],
		'a': ['border-bottom'],
		'hr': ['width', 'height'],
		'ul li': ['list-style-type', 'list-style-position', 'list-style-image',
			'background-image', 'background-position', 'background-repeat']
	},
			
	selectors: ['body', 'p', 'a', 'strong', 'em', 'h1', 'h2', 
		'h3', 'h4', 'hr', 'ul', 'ul li', 'ol', 'ol li'],
		
	addRule: function(selector, styles, sheet) {
		var css = '';
		for (prop in styles) {
			if (typeof(prop) == 'function') { continue; }
			css += prop + ':' + styles[prop] 
			if (selector == 'a' && (prop == 'color' || prop == 'text-decoration')) {
				css += ' !important';
			}
			css += ';'
		}		
		if (sheet.insertRule) {
			var rule = selector + ' {' + css + '}';
			sheet.insertRule(rule, sheet.cssRules.length);
		}
		else if (sheet.addRule) {
			sheet.addRule(selector, css);
		}
	},

	getStyles: function(el, props) {
		var result = {};
		for (var i = 0; i < props.length; i++) {
			result[props[i]] = Element.getStyle(el, props[i]);
		}
		return result;
	},

	copyStyles: function(el, doc, classes) {
		var sheet = doc.styleSheets[doc.styleSheets.length - 1];	
		for (var i = 0; i < this.selectors.length; i++) {
			var selector = this.selectors[i];
			var p = el; 
			var names = selector.split(/\s/);
			for (var n = 0; n < names.length; n++) {
				if (names[n] != 'body') {
					var e = document.createElement(names[n]);
					if (names[n] == 'a') e.href = '#';
					p.appendChild(e);
					p = e;
				}
			}
			
			var styles = this.getStyles(p, this.properties['*']);
			var props = this.properties[selector];
			if (isDefined(props)) {
				Object.extend(styles, this.getStyles(p, props));
			}
			if (selector == 'body') {
				styles['background-color'] = Styles.getBackgroundColor(p);
			}
			this.addRule(selector, styles, sheet);
		}
		if (classes) {
			for (var i = 0; i < classes.length; i++) {
				var e = document.createElement('span');
				e.className = classes[i];
				el.appendChild(e);
				var styles = this.getStyles(e, this.properties['*']);
				this.addRule('.' + classes[i], styles, sheet);
			}
		}
	}
}

riot.setupTinyMCEContent = function(editorId, body, doc) {
	var style = doc.createElement('style');
	style.type = 'text/css';
	var head = doc.getElementsByTagName('head')[0];
	head.appendChild(style);
		
	var e = riot.activeEditor.element;
	var clone = e.cloneNode(false);
	Element.hide(clone);
	RElement.insertBefore(clone, e);
	riot.stylesheetMaker.copyStyles(clone, doc, riot.tinyMCEStyles);
	Element.remove(clone);
	
	body.style.paddingLeft = '5px';
	
	// Add a print margin ...

	var bg = Styles.getBackgroundColor(e).parseColor();
	var brightness = 0;
	$R(0,2).each(function(i) { brightness += parseInt(bg.slice(i*2+1,i*2+3), 16) });
	brightness /= 3;
	var bgImage = brightness > 227 ? 'margin.gif' : 'margin_hi.gif';

	var editorWidth = riot.activeEditor.width;
	var componentWidth = riot.activeEditor.component.element.offsetWidth;
	var margin = editorWidth - componentWidth;
	if (margin > 0) {
		body.style.paddingRight = margin + 'px';
		body.style.backgroundImage = 'url(' + Resources.resolveUrl(bgImage) + ')';
		body.style.backgroundRepeat = 'repeat-y';
		body.style.backgroundPosition = componentWidth + 'px';
		body.style.backgroundAttachment = 'fixed';
	}
}

riot.initTinyMCEInstance = function(inst) {
	//Reset -5px margin set by TinyMCE in strict_loading_mode
	riot.activeEditor.width = inst.iframeElement.offsetWidth;
	inst.iframeElement.style.marginBottom = '0';
}

riot.initTinyMCE = function() {
	if (!riot.tinyMCEInitialized) {
		tinyMCE.init(riot.tinyMCEConfig);
		riot.tinyMCEInitialized = true;
	}
}

riot.tinyMCEConfig = {
	mode: 'none',
	add_unload_trigger: false,
	strict_loading_mode: true,
	setupcontent_callback: 'riot.setupTinyMCEContent',
	init_instance_callback: 'riot.initTinyMCEInstance',
	relative_urls: false,
	theme: 'advanced',
	theme_advanced_layout_manager: 'RowLayout',
	theme_advanced_containers_default_align: 'left',
	theme_advanced_containers: 'buttons1, mceEditor, mceStatusbar',
	theme_advanced_container_buttons1: 'formatselect,bold,italic,sup,bullist,numlist,outdent,indent,hr,link,unlink,anchor,code,undo,redo,charmap',
	theme_advanced_blockformats: 'p,h3,h4',
	valid_elements: '+a[href|target|name],-strong/b,-em/i,h3/h2/h1,h4/h5/h6,p,br,hr,ul,ol,li,blockquote,sub,sup,span[class<mailto]'
}
	
ComponentEditor.getEditorConfigs(function(configs) {
	if (configs && configs.tinyMCE) {
		Object.extend(riot.tinyMCEConfig, configs.tinyMCE);
		var styles = riot.tinyMCEConfig.theme_advanced_styles;
		if (styles) {
			riot.tinyMCEStyles = styles.split(';').collect(function(pair) {
		      return pair.split('=')[1];
		    });
		}
	}
});
