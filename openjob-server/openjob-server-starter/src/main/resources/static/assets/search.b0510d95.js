import{h as _,ar as C,r as k,aH as R,K as T,a9 as n,j as B,k as I,D as l,x as u,l as Q,B as H,C as N,J as O}from"./vue.bd0669ce.js";import{u as A}from"./vue-i18n.cjs.2a00fe88.js";import{u as D}from"./tagsViewRoutes.054c4a6f.js";import{_ as M}from"./_plugin-vue_export-helper.c27b6911.js";import"./_commonjsHelpers.35101cd5.js";import"./storage.b628b270.js";const U={class:"layout-search-dialog"},$=_({name:"layoutBreadcrumbSearch"}),b=_({...$,setup(j,{expose:d}){const f=D(),{tagsViewRoutes:h}=C(f),m=k(),{t:w}=A(),p=R(),o=T({isShowSearch:!1,menuQuery:"",tagsViewList:[]}),S=()=>{o.menuQuery="",o.isShowSearch=!0,v(),O(()=>{setTimeout(()=>{m.value.focus()})})},V=()=>{o.isShowSearch=!1},g=(e,t)=>{let s=e?o.tagsViewList.filter(L(e)):o.tagsViewList;t(s)},L=e=>t=>t.path.toLowerCase().indexOf(e.toLowerCase())>-1||t.meta.title.toLowerCase().indexOf(e.toLowerCase())>-1||w(t.meta.title).indexOf(e.toLowerCase())>-1,v=()=>{if(o.tagsViewList.length>0)return!1;h.value.map(e=>{var t;(t=e.meta)!=null&&t.isHide||o.tagsViewList.push({...e})})},x=e=>{var c,r,i;let{path:t,redirect:s}=e;((c=e.meta)==null?void 0:c.isLink)&&!((r=e.meta)!=null&&r.isIframe)?window.open((i=e.meta)==null?void 0:i.isLink):s?p.push(s):p.push(t),V()};return d({openSearch:S}),(e,t)=>{const s=n("ele-Search"),c=n("el-icon"),r=n("SvgIcon"),i=n("el-autocomplete"),y=n("el-dialog");return B(),I("div",U,[l(y,{modelValue:o.isShowSearch,"onUpdate:modelValue":t[1]||(t[1]=a=>o.isShowSearch=a),"destroy-on-close":"","show-close":!1},{footer:u(()=>[l(i,{modelValue:o.menuQuery,"onUpdate:modelValue":t[0]||(t[0]=a=>o.menuQuery=a),"fetch-suggestions":g,placeholder:e.$t("message.user.searchPlaceholder"),ref_key:"layoutMenuAutocompleteRef",ref:m,onSelect:x,"fit-input-width":!0},{prefix:u(()=>[l(c,{class:"el-input__icon"},{default:u(()=>[l(s)]),_:1})]),default:u(({item:a})=>[Q("div",null,[l(r,{name:a.meta.icon,class:"mr5"},null,8,["name"]),H(" "+N(e.$t(a.meta.title)),1)])]),_:1},8,["modelValue","placeholder"])]),_:1},8,["modelValue"])])}}});const G=M(b,[["__scopeId","data-v-a11c6061"]]);export{G as default};