/**
 * Copyright (C) 2005-2014 Rivet Logic Corporation.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */

AUI.add('evernote-portlet', function (Y, NAME) {
	
    Y.Evernote = Y.Base.create('evernote-portlet', Y.Base, [], {
    	
        loadNotes : function() {
        	var me = this;
        	var pns = this.get('portletNamespace');
            this.executeAjax({method: 'GET', data: {}},
                             function(d) {
            					me.renderNoteList({notes: d});
                            },
                            this.get('loadNotesURL'));
        },
        
        /**
         *  Executes an ajax call
         * 
         */
        executeAjax: function(configuration, callback, url) {
            Y.io.request(url, {
                dataType: 'json',
                data: configuration.data,
                on: {
                    success: function(e) {
                        var data = this.get('responseData');
                        callback(data); 
                    }
                }
            });
        },
        
        showAjaxLoader: function() {
        	var html = Y.one("#ajax-loader-icon").getHTML();	
        	Y.one("#notes-list").append(html);
        },
        
        hideAjaxLoader: function(){
        	Y.one("#notes-list").one('img').remove();
        },
       
        renderNoteList: function(data) {
            var instance = this,
            	source   = Y.one('#note-list-template').getHTML(),
                template = Y.Handlebars.compile(source),
                html = template(data),
                pns = this.get('portletNamespace'),
                notesList = Y.one("#notes-list"),
                frameContainer = Y.one('#' + pns + 'preview-frame-container');
            
            notesList.get('childNodes').remove();
            notesList.addClass("evernote");
            notesList.append(html);
            
            /* set YUI Frame */
            var frame = new Y.Frame({
            	container : frameContainer,
            	content: Y.one("#frame-content").getHTML(),
            	extracss: instance.CONSTANTS.FRAME_MESSAGE_STYLE
            });
            /* Overriding Frame Styles after loading */
            frame.after("ready", function() {
                frameContainer.all(".ajax-loader").hide();
            	frame._iframe.setAttribute("style", instance.CONSTANTS.FRAME_BORDER_STYLE);
            	var fi = frame.getInstance();
            	fi.one("html").setAttribute("style", instance.CONSTANTS.FRAME_HTML_STYLE);
            	fi.one("body").setAttribute("style", instance.CONSTANTS.FRAME_BODY_STYLE);
            });
            frame.render();
            
            this.set('frame', frame);
        },

        listenerNotes: function() {
        	var me = this;
        	var pns = this.get('portletNamespace');
            Y.one("#notes-list").delegate("click", function(e) {
                var guid = e.currentTarget.get("id");
                var title = e.currentTarget.html();
    			Y.one('#' + pns + 'noteTitleDelete').setAttribute("value", title);
                me.getNote(guid);
            }, "li.note-item");
        },
        
        renderIframe: function(data) {
        	var noteContent = enml.HTMLOfENML(data.noteContent),
				editNoteURL = data.editNoteURL,
				pns = this.get('portletNamespace');

        	this.get('frame').set('content', noteContent);
        	Y.one("#"+pns+"editNoteURL").setAttribute('href', editNoteURL);
			Y.one('#' + pns + 'noteGuidDelete').setAttribute("value", data.guid);
        },

        getNote: function(guid) {
        	var me = this,
        		pns = this.get('portletNamespace'),
        		data = Liferay.Util.ns(
                    this.get('portletNamespace'),
                    {
                        noteGuid: guid
                    });
        	me.get('frame').set('content', me.CONSTANTS.NOT_CONTENT);
        	var frameContainer = Y.one('#' + pns + 'preview-frame-container');
        	frameContainer.all(".ajax-loader").show();
            this.executeAjax(
            	{data: data},
                function(d) {
            		me.renderIframe(d);
            		frameContainer.all(".ajax-loader").hide();
                },
                this.get('selectNoteURL'));
        },
        
        deleteNoteListener: function(){
        	var container = this.get("contentBox"),
        		pns = this.get("portletNamespace"),
        		element = container.one("#"+pns+"deleteNote");
        		
        	element.on("click", function(e){
        		var noteGuid = container.one("#"+pns+"noteGuidDelete").getAttribute("value"),
        			noteTitle = container.one("#"+pns+"noteTitleDelete").getAttribute("value"),
        			confirmMessage = Y.Lang.sub(Liferay.Language.get("delete-note-confirmation-message"), {
                		0: noteTitle
        			});
        		
        		if (noteGuid != null && noteGuid.length > 0) {
        			if (confirm(confirmMessage)) {
        				container.one("#"+pns+"deleteNoteForm").submit();
        			}
        		} else {
        			alert(Liferay.Language.get("delete-note-alert"));
        		}
        		
        	});
        },
        
        editNoteListener: function(){
        	var container = this.get("contentBox"),
        		pns = this.get("portletNamespace"),
        		editNoteButton = container.one("#"+pns+"editNoteURL");
        	
        	editNoteButton.on("click", function(e){
        		if (e.target.getAttribute("href") == "#") {
        			e.preventDefault();
        			alert(Liferay.Language.get("edit-note-alert"));
        		} 
        	});
        },
        
        loadMoreNotes: function(){
        	var instance = this,
        		container = instance.get("contentBox"),
        		notesList = container.one("#notes-list");
        	
        	if (notesList) {
        		notesList.delegate("click", function(e){
        			instance.showAjaxLoader();
        			var notebook = e.currentTarget,
                        guid = notebook.getAttribute("id"),		
    				    notes = notebook.get("parentNode").get("childNodes"),
    				    countLI = 0,
                        loadMoreNotesURL = instance.get("loadMoreNotesURL"),
                        namespace = instance.get("portletNamespace"),
                        notebookData = {};
        			
        			notes.each(function(note){
        				if (note.get("nodeName") == instance.CONSTANTS.LI_ELEMENT) countLI++;
        			});
                    
                    notebookData[namespace+"countLI"] = countLI;
                    notebookData[namespace+"notebookGuid"] = guid;
    					
    				Y.io.request(loadMoreNotesURL, {
    					data: notebookData,
    					dataType: 'json',
    					on: {
    						success: function() {
    							var loadMore = this.get('responseData').loadMore;
    							var noteList = this.get('responseData').noteList;
    							
    							var loadMoreLI = Y.one('#' + guid);
    							var noteListUL = loadMoreLI.ancestor();
    							
    							if(loadMore){
    								loadMoreLI = loadMoreLI.cloneNode(true);
    								Y.one('#' + guid).remove();
    							}
    							else{
    								loadMoreLI.remove();
    							}
    	
    							for(var i = 0; i < noteList.length; i++){
    								var noteLi = Y.Lang.sub(
    										Y.one('#note-list-li').get('innerHTML'), 
    										noteList[i]);							
    								noteListUL.appendChild(noteLi);
    							} 
    							
    							if(loadMore) noteListUL.appendChild(loadMoreLI);
    							instance.hideAjaxLoader();
    			  			}
    					}
    				});	
        		}, ".load-more-notes");
        	}
        },
        
        toggleNotebookListener: function() {
        	var instance = this,
    		container = instance.get("contentBox"),
    		notesList = container.one("#notes-list");
    	
        	if (notesList) {
        		notesList.delegate("click", function(e){
        			
        			var obj = e.currentTarget,
    					target = obj.next();
        			
    				if (obj.hasClass("collapsed")) {
    					obj.replaceClass("collapsed", "expanded");
    					target.show();
    				} else {
    					obj.replaceClass("expanded", "collapsed");
    					target.hide();
    				}
    				
        		}, ".toggle");
        	}
        },
        
        initializer: function() {
            this.loadNotes();
            this.listenerNotes();
            this.deleteNoteListener();
            this.editNoteListener();
            this.loadMoreNotes();
            this.toggleNotebookListener();
        },
        
        CONSTANTS: {
        	FRAME_BORDER_STYLE	: "border: 1px solid #A9A9A9;",
    		FRAME_HTML_STYLE	: "margin: 10px;",
    		FRAME_BODY_STYLE	: "background: none repeat scroll 0 0 rgba(0, 0, 0, 0);",
    		FRAME_MESSAGE_STYLE : ".select-note {color: #A9A9A9}",
    		LI_ELEMENT			: "LI",
    		NOT_CONTENT			: ""
        }
    },
    {
        ATTRS: {

            selectNoteURL: {
                value: ''
            },
            
            loadMoreNotesURL: {
                value: null
            },

            portletNamespace: {
                value: ''
            },
            
            loadNotesURL: {
                value: ''
            },

            deleteNoteURL: {
                value: false
            },
            
            frame: {
            	value: null
            },
            
            contentBox: {
            	value: null
            }

        }
    }
)},
    '@VERSION@',
    {
    "requires": [
        'yui-base', 
        'base-build', 
        'node', 
        'event', 
        'node',
        'aui-io-request',                 
        'event',
        'frame',
        'aui-modal',
        'aui-tooltip',
        'handlebars',
        'aui-datatable',
        'aui-pagination',
        'datatable-sort',
        'io-base'
    ]
});