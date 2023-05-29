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

        linkCode: "",
        urlAccountToNumber: "",
        urlAmount: "",
        urlDescription: ""
    },
    methods:{
        getData: function(){
            const urlParams = new URLSearchParams(window.location.search);
            this.linkCode = urlParams.get('linkCode');
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
            }else{
                this.transfer.apply();
            }
        },
        transfer: function(){
            let config = {
                headers: {
                    'content-type': 'application/x-www-form-urlencoded'
                }
            }
            axios.post(`/api/transactions/pay_with_link?linkCode=${this.linkCode}&fromAccountNumber=${this.accountFromNumber}`)
                .then((response) => {
                    this.modal.show();
                })
                .catch((error) =>{
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })

            /*
            axios.post(`/api/transactions?fromAccountNumber=${this.accountFromNumber}&toAccountNumber=${this.accountToNumber}&amount=${this.amount}&description=${this.description}`,config)
                .then(response => {
                    this.modal.show();
                })
                .catch((error) =>{
                    this.errorMsg = error.response.data;
                    this.errorToats.show();
                })

             */
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
    },
    mounted: function(){
        this.errorToats = new bootstrap.Toast(document.getElementById('danger-toast'));
        this.modal = new bootstrap.Modal(document.getElementById('otpModal'));
        this.okmodal = new bootstrap.Modal(document.getElementById('okModal'));
        this.getData();
    }
})