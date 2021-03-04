import Vue from 'vue'
import App from './App.vue'
import { BootstrapVue } from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import './scss/app.scss'
import router from './router'

// Default components
Vue.component('navbar-component', require('./components/module/NavbarComponent.vue').default);

Vue.config.productionTip = false

Vue.use(BootstrapVue)

new Vue({
  render: h => h(App),
  router
}).$mount('#app')
