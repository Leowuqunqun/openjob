import{h as c,K as u,a9 as p,j as e,k as n,l as s,C as t,A as l,F as v,Z as b,v as w}from"./vue.bd0669ce.js";import{_ as h}from"./_plugin-vue_export-helper.c27b6911.js";const y={class:"layout-navbars-breadcrumb-user-news"},k={class:"head-box"},g={class:"head-box-title"},x={class:"content-box"},C={class:"content-box-msg"},f={class:"content-box-time"},L=c({name:"layoutBreadcrumbUserNews"}),B=c({...L,setup(N){const o=u({newsList:[{label:"关于版本发布的通知",value:"vue-next-admin，基于 vue3 + CompositionAPI + typescript + vite + element plus，正式发布时间：2021年02月28日！",time:"2020-12-08"},{label:"关于学习交流的通知",value:"QQ群号码 665452019，欢迎小伙伴入群学习交流探讨！",time:"2020-12-08"}]}),r=()=>{o.newsList=[]},d=()=>{window.open("https://gitee.com/lyt-top/vue-next-admin")};return(a,$)=>{const _=p("el-empty");return e(),n("div",y,[s("div",k,[s("div",g,t(a.$t("message.user.newTitle")),1),o.newsList.length>0?(e(),n("div",{key:0,class:"head-box-btn",onClick:r},t(a.$t("message.user.newBtn")),1)):l("",!0)]),s("div",x,[o.newsList.length>0?(e(!0),n(v,{key:0},b(o.newsList,(i,m)=>(e(),n("div",{class:"content-box-item",key:m},[s("div",null,t(i.label),1),s("div",C,t(i.value),1),s("div",f,t(i.time),1)]))),128)):(e(),w(_,{key:1,description:a.$t("message.user.newDesc")},null,8,["description"]))]),o.newsList.length>0?(e(),n("div",{key:0,class:"foot-box",onClick:d},t(a.$t("message.user.newGo")),1)):l("",!0)])}}});const D=h(B,[["__scopeId","data-v-423e936b"]]);export{D as default};