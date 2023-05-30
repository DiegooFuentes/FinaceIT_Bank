const app = new Vue({
    el: '#app',
    data:{
        clientInfo: {},
        errorToats: null,
        errorMsg: null,
        uf: "",
        dolar: "",
        euro: "",
        utm: "",
    },
    methods:{
        getData: function(){
            axios.get("/api/clients/current")
                .then((response) => {
                    //get client ifo
                    this.clientInfo = response.data;
                })
                .catch((error)=>{
                    // handle error
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        signOut: function(){
            axios.post('/api/logout')
                .then(response => window.location.href="/web/index.html")
                .catch(() =>{
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        create: function(){
            axios.post('/api/clients/current/accounts')
                .then(response => window.location.reload())
                .catch((error) =>{
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })
        },
        getIndicadorUF: function () {
              axios
                .get('/api/indicator/uf')
                .then((response) => {
                  this.uf = response.data.serie[0].valor;
                })
                .catch((error) => {
                  this.errorMsg = 'Error getting UF indicator';
                  this.errorToats.show();
                });
        },
        getIndicadorDolar: function() {
              axios.get("/api/indicator/dolar")
                .then((response) => {
                  this.dolar = response.data.serie[0].valor;
                })
                .catch((error) => {
                  this.errorMsg = "Error getting Dolar indicator";
                  this.errorToats.show();
                });
            },
            getIndicadorEuro: function() {
              axios.get("/api/indicator/euro")
                .then((response) => {
                  this.euro = response.data.serie[0].valor;
                })
                .catch((error) => {
                  this.errorMsg = "Error getting Euro indicator";
                  this.errorToats.show();
                });
            },
            getIndicadorUTM: function() {
              axios.get("/api/indicator/utm")
                .then((response) => {
                  this.utm = response.data.serie[0].valor;
                })
                .catch((error) => {
                  this.errorMsg = "Error getting UTM indicator";
                  this.errorToats.show();
                });
            },
            formatNumber: function(number) {
                  return Number(number).toLocaleString('es-CL', {
                  style: 'currency',
                  currency: 'CLP',
                    minimumFractionDigits: 2,
                    maximumFractionDigits: 2
                  });
            },
            formatIntNumber: function(number) {
                  return Number(number).toLocaleString('es-CL', {
                  style: 'currency',
                  currency: 'CLP',
                  });
            },
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.getData();
        this.getIndicadorUF();
        this.getIndicadorDolar();
        this.getIndicadorEuro();
        this.getIndicadorUTM();
    }
})