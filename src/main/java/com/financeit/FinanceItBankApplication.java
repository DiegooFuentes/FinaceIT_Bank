package com.financeit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FinanceItBankApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceItBankApplication.class, args);
	}

/*
	@Bean
	public CommandLineRunner initData() {
		return (args) -> {

			Client melba = createClient("Melba","Morel","melba@mindhub.com","123");
			Account melbaAcc1 = createAccount("VIN001", LocalDateTime.now(),127000.0,melba);
			debitTransaction(990.0,"New TV",LocalDateTime.now(),melbaAcc1);
			debitTransaction(632.0,"Gas",LocalDateTime.now(),melbaAcc1);
			creditTransaction(8343.0,"Designer sofa",LocalDateTime.now(),melbaAcc1);

			Account melbaAcc2 = createAccount("VIN002",LocalDateTime.now().plusDays(1),7500.0,melba);
			debitTransaction(2000.0,"New Car",LocalDateTime.now(),melbaAcc2);
			debitTransaction(340.0,"Gas",LocalDateTime.now(),melbaAcc2);

			Client diego = createClient("Diego","Fuentes","diego@mindhub.com","123");
			Account diegoAcct1 = createAccount("VIN003",LocalDateTime.now(),8560.0,diego);
			debitTransaction(200.0,"New Phone",LocalDateTime.now(),diegoAcct1);
			creditTransaction(24.0,"Dinner",LocalDateTime.now(),diegoAcct1);

			Account diegoAcct2 = createAccount("VIN004",LocalDateTime.now(),10060.0,diego);
			creditTransaction(3045.0,"Breakfast",LocalDateTime.now(),diegoAcct2);

			Loan hipotecario = createLoan("Hipotecario",40000, List.of(12,24,36,48,60));
			Loan personal = createLoan("Personal", 100000,List.of(6,12,24));
			Loan automotriz = createLoan("Automotriz", 300000,List.of(6,12,24));

			ClientLoan loanMelba1 = createClientLoan(400000,hipotecario.getPayments().get(4),melba,hipotecario);
			ClientLoan loanMelba2 = createClientLoan(50000,12,melba,personal);
			ClientLoan loanDiego1 = createClientLoan(100000,24,diego,personal);
			ClientLoan loanDiego2 = createClientLoan(200000,36,diego,automotriz);

			Card melbaCard1 = createCard(CardType.DEBIT,CardColor.GOLD,"4019 2581 3319 2620","733",melba);
			Card melbaCard2 = createCard(CardType.CREDIT,CardColor.TITANIUM,"4004 0889 6620 6636","192",melba);
			Card diegoCard1 = createCard(CardType.CREDIT,CardColor.SILVER,"4001 0279 4962 8097","745",diego);

			Client Admin = createClient("Admin","Admin","admin@admin.com","admin");


		};
	}

	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TransactionRepository transactionRepository;
	@Autowired
	private LoanRepository loanRepository;
	@Autowired
	private ClientLoanRepository clientLoanRepository;
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public Client createClient(String firstName,String lastName, String email,String password){

		Client client = new Client(firstName,lastName,email,passwordEncoder.encode(password));
		clientRepository.save(client);
		return client;

	}

	public Account createAccount(String number, LocalDateTime creationDate, double balance, Client client){
		Account account = new Account(number, creationDate,balance);
		account.setClient(client);
		accountRepository.save(account);
		return account;
	}

	public void debitTransaction(double amount, String description, LocalDateTime creationDate, Account account ){
		TransactionType debit = TransactionType.DEBIT;
		Transaction transaction = new Transaction(debit,amount,description,creationDate);
		transaction.setAccount(account);
		transactionRepository.save(transaction);
	}

	public void creditTransaction(double amount, String description,LocalDateTime creationDate , Account account){
		TransactionType credit = TransactionType.CREDIT;
		Transaction transaction = new Transaction(credit,amount,description, creationDate);
		transaction.setAccount(account);
		transactionRepository.save(transaction);
	}

	public Loan createLoan(String name, double maxAmount,List<Integer> cuotas){
		Loan loan = new Loan(name, maxAmount, cuotas);
		loanRepository.save(loan);
		return loan;
	}

	public ClientLoan createClientLoan(double amount,int payments, Client client, Loan loan ){

		ClientLoan clientLoan = new ClientLoan(amount, payments, client, loan);
		clientLoanRepository.save(clientLoan);
		return clientLoan;
	}

	public Card createCard(CardType type,CardColor color,String number, String cvv,Client client){
		String name = client.getFirstName() + " " + client.getLastName();
		Card card = new Card(name,type,color,number,cvv,LocalDateTime.now(),LocalDateTime.now().plusYears(5));
		card.setClient(client);
		cardRepository.save(card);
		return card;
	}

*/

}
