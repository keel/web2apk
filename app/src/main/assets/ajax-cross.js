/* ajaxhook (https://github.com/keel/ajaxhook) */
function __ajax_hook(e){window.__xhr_org=window.__xhr_org||XMLHttpRequest,XMLHttpRequest=function(){var o=new window.__xhr_org;this.xhr=o,this.hookConfig=e;var r=this;r.isHook=1,r.DONE=4,r.LOADING=3,r.HEADERS_RECEIVED=2,r.OPENED=1,r.UNSENT=0;var n=0,t=0,s=["readyState","response","responseType","responseText","responseURL","responseXML","status","statusText","timeout","upload","withCredentials"];r.newPropMap={},r.syncProps=function(){for(n=0,t=s.length;n<t;n++){var e=s[n];if(!r.newPropMap[e]){if("responseText"===e&&"text"!==r.xhr.responseType&&""!==r.xhr.responseType)continue;if("responseXML"===e&&"document"!==r.xhr.responseType&&""!==r.xhr.responseType)continue;r[e]=r.xhr[e]}}},r.updateProps=function(){r.xhr.readyState<2&&(r.xhr.responseType=r.responseType||r.xhr.responseType,r.xhr.timeout=r.timeout||r.xhr.timeout,r.xhr.withCredentials=r.withCredentials||r.xhr.withCredentials)},r.regEvent=function(e){e="on"+e,r.xhr[e]=function(o){if(r.syncProps(),r.hookConfig&&r.hookConfig[e])return r.hookConfig[e].apply(r,[o]);r[e]&&r[e](o)}},r.regFun=function(e){r[e]=function(){r.updateProps();var o=[].slice.call(arguments);return r.hookConfig&&r.hookConfig[e]?r.hookConfig[e].apply(r,o):r.xhr[e].apply(r.xhr,o)}},r.doReg=function(e,o){for(n=0,t=o.length;n<t;n++)r[e](o[n])},r.updateXhr=function(e,o){r[e]=o,r.newPropMap[e]=1};r.doReg("regFun",["abort","send","getResponseHeader","getAllResponseHeaders","overrideMimeType","setRequestHeader","open"]),r.doReg("regEvent",["readystatechange","loadstart","load","loadend","progress","error","abort","timeout"])}};function __ajax_unhook(){XMLHttpRequest=window.__xhr_org||XMLHttpRequest,window.__xhr_org=null};

//hook http/https requests, send to local server, so the server in android will send these requests to cross domain.
//hook外部http/https请求,转发到本地server,由server直接请求以实现跨域
__ajax_hook({
  'open': function(method, url, async, user, password) {
    if (url.indexOf('http') === 0) { //以http开头的请求(包含https),为外部请求的绝对地址
      console.log('====> _hook[open]:'+method+','+url+','+async+','+user+','+password);
      this.oldUrl = url;
      url = 'http://localhost:8888/__cross__?u='+this.oldUrl;
    }
    return this.xhr.open(method, url, async, user, password);
  },
});


