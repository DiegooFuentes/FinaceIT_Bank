var app = new Vue({
    el:"#app",
    data:{
        clientInfo: {},
        clientAccounts: [],
        clientAccountsTo: [],
        debitCards: [],
        errorToats: null,
        errorMsg: null,
        accountFromNumber: "VIN",
        accountToNumber: "VIN",
        transferType: "own",
        amount: 0,
        description: "",
        passwordTOTP: "",
        prompt: "",
        loading: false // Variable de estado para controlar la carga
    },
    methods:{
        getData: function(){
            axios.get("/api/clients/current/accounts")
                .then((response) => {
                    //get client ifo
                    this.clientAccounts = response.data;
                })
                .catch((error) => {
                    this.errorMsg = "Error getting data";
                    this.errorToats.show();
                })
        },
        formatDate: function(date){
            return new Date(date).toLocaleDateString('en-gb');
        },
        checkTransfer: function(){
            if(this.accountFromNumber == "VIN"){
                this.errorMsg = "You must select an origin account";
                this.errorToats.show();
            }
            else if(this.accountToNumber == "VIN"){
                this.errorMsg = "You must select a destination account";
                this.errorToats.show();
            }else if(this.amount == 0){
                this.errorMsg = "You must indicate an amount";
                this.errorToats.show();
            }
            else if(this.description.length <= 0){
                this.errorMsg = "You must indicate a description";
                this.errorToats.show();
            }else{
                this.transfer.apply();
            }
        },
        transfer: function(){
            if(this.loading){
                return; // Si ya está cargando, no hacer nada
            }

            this.loading = true;

            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post(`/api/transactions?fromAccountNumber=${this.accountFromNumber}&toAccountNumber=${this.accountToNumber}&amount=${this.amount}&description=${this.description}`,config)
                .then(response => {
                    this.modal.show();
                })
                .catch((error) =>{
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })
                .finally(() => {
                    this.loading = false; // Restablecer la variable de estado como no cargando
                });
        },
        validateOTP: function() {
            axios.post(`/api/transactions/validate?dynamicPassword=${this.passwordTOTP}`)
                .then(response => {
                    this.modal.hide();
                    this.okmodal.show();
                })
                .catch((error) =>{
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })
        },
        changedType: function(){
            this.accountFromNumber = "VIN";
            this.accountToNumber = "VIN";
        },
        changedFrom: function(){
            if(this.transferType == "own"){
                this.clientAccountsTo = this.clientAccounts.filter(account => account.number != this.accountFromNumber);
                this.accountToNumber = "VIN";
            }
        },
        finish: function(){
            window.location.reload();
        },
        signOut: function(){
            axios.post('/api/logout')
                .then(response => window.location.href="/web/index.html")
                .catch(() =>{
                    this.errorMsg = "Sign out failed"
                    this.errorToats.show();
                })
        },
        sendPrompt: function() {
            axios.post(`/api/questions/send?message=${this.prompt}`)
                .then(response => {
                     this.modal.show();
                })
                .catch((error) =>{
                     this.errorMsg = error.response.data;
                     this.errorToats.show();
                })
        },
        getCLientInfo: function(){
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
        }
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.modal = new bootstrap.Modal(document.getElementById('otpModal'));
        this.okmodal = new bootstrap.Modal(document.getElementById('okModal'));
        this.getData();
    }
})