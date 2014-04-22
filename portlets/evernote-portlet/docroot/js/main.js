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
        	var html = "<img src='../images/ajax-loader-big.gif' />";
        	Y.one("#notes-list").append(html);
        },
        
        renderNoteList: function(data) {
            var source   = Y.one('#note-list-template').getHTML(),
                template = Y.Handlebars.compile(source),
                html = template(data),
                pns = this.get('portletNamespace');
            
            Y.one("#notes-list").get('childNodes').remove();
            Y.one("#notes-list").addClass("evernote");
            Y.one("#notes-list").append(html);
            
            // set YUI Frame
            var frame = new Y.Frame({
            	container : '#' + pns + 'preview-frame-container',
            	content: "<p>Please select a note from left menu</p>",
            	extracss: "p {color: #A9A9A9}"
            });
            frame.after("ready", function() {
            	frame._iframe.setAttribute("style", "border: 1px solid #A9A9A9;");
            	var fi = frame.getInstance();
            	fi.one("html").setAttribute("style", "margin: 10px;");
            	fi.one("body").setAttribute("style", "background: none repeat scroll 0 0 rgba(0, 0, 0, 0);");
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
			Y.one('#' + pns + 'editNoteURL').setAttribute('href', editNoteURL);
			Y.one('#' + pns + 'editNoteURL').setAttribute('target', '_blank');
			Y.one('#' + pns + 'noteGuidDelete').setAttribute("value", data.guid);
        },

        getNote: function(guid) {
        	var me = this,
        		data = Liferay.Util.ns(
                    this.get('portletNamespace'),
                    {
                        noteGuid: guid
                    });
        	
            this.executeAjax(
            	{data: data},
                function(d) {
            		me.renderIframe(d);
                },
                this.get('selectNoteURL'));
        },

        initializer: function() {
            this.loadNotes();
            this.listenerNotes();
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
            }

        }
    }
)},
    '@VERSION@',
    {
        "requires": ["yui-base", "base-build", "node", "event", 'node',
    'aui-io-request',                 
    'event',
    'frame',
    'aui-modal',
    'aui-tooltip',
    'handlebars',
    'aui-datatable',
    'aui-pagination',
    'datatable-sort',
    'io-base']
});