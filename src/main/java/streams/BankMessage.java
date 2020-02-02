package streams;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"age",
"job",
"marital",
"education",
"def",
"balance",
"housing",
"loan",
"contact",
"day",
"month",
"duration",
"campaign",
"pdays",
"previous",
"poutcome"
})

public class BankMessage {

	@JsonProperty("age")
	private String age;
	@JsonProperty("job")
	private String job;
	@JsonProperty("marital")
	private String marital;
	@JsonProperty("education")
	private String education;
	@JsonProperty("def")
	private String def;
	@JsonProperty("balance")
	private String balance;
	@JsonProperty("housing")
	private String housing;
	@JsonProperty("loan")
	private String loan;
	@JsonProperty("contact")
	private String contact;
	@JsonProperty("day")
	private String day;
	@JsonProperty("month")
	private String month;
	@JsonProperty("duration")
	private String duration;
	@JsonProperty("campaign")
	private String campaign;
	@JsonProperty("pdays")
	private String pdays;
	@JsonProperty("previous")
	private String previous;
	@JsonProperty("poutcome")
	private String poutcome;
	@JsonProperty("outcome")
	private String outcome;

	@JsonProperty("age")
	public String getAge() {
		return age;
	}

	@JsonProperty("age")
	public void setAge(String age) {
		this.age = age;
	}

	@JsonProperty("job")
	public String getJob() {
		return job;
	}

	@JsonProperty("job")
	public void setJob(String job) {
		this.job = job;
	}

	@JsonProperty("marital")
	public String getMarital() {
		return marital;
	}

	@JsonProperty("marital")
	public void setMarital(String marital) {
		this.marital = marital;
	}

	@JsonProperty("education")
	public String getEducation() {
		return education;
	}

	@JsonProperty("education")
	public void setEducation(String education) {
		this.education = education;
	}

	@JsonProperty("default")
	public String getDef() {
		return def;
	}

	@JsonProperty("default")
	public void setDef(String def) {
		this.def = def;
	}

	@JsonProperty("balance")
	public String getBalance() {
		return balance;
	}

	@JsonProperty("balance")
	public void setBalance(String balance) {
		this.balance = balance;
	}

	@JsonProperty("housing")
	public String getHousing() {
		return housing;
	}

	@JsonProperty("housing")
	public void setHousing(String housing) {
		this.housing = housing;
	}

	@JsonProperty("loan")
	public String getLoan() {
		return loan;
	}

	@JsonProperty("loan")
	public void setLoan(String loan) {
		this.loan = loan;
	}

	@JsonProperty("contact")
	public String getContact() {
		return contact;
	}

	@JsonProperty("contact")
	public void setContact(String contact) {
		this.contact = contact;
	}

	@JsonProperty("day")
	public String getDay() {
		return day;
	}

	@JsonProperty("day")
	public void setDay(String day) {
		this.day = day;
	}

	@JsonProperty("month")
	public String getMonth() {
		return month;
	}

	@JsonProperty("month")
	public void setMonth(String month) {
		this.month = month;
	}

	@JsonProperty("duration")
	public String getDuration() {
		return duration;
	}

	@JsonProperty("duration")
	public void setDuration(String duration) {
		this.duration = duration;
	}

	@JsonProperty("campaign")
	public String getCampaign() {
		return campaign;
	}

	@JsonProperty("campaign")
	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	@JsonProperty("pdays")
	public String getPdays() {
		return pdays;
	}

	@JsonProperty("pdays")
	public void setPdays(String pdays) {
		this.pdays = pdays;
	}

	@JsonProperty("previous")
	public String getPrevious() {
		return previous;
	}

	@JsonProperty("previous")
	public void setPrevious(String previous) {
		this.previous = previous;
	}

	@JsonProperty("poutcome")
	public String getPoutcome() {
		return poutcome;
	}

	@JsonProperty("poutcome")
	public void setPoutcome(String poutcome) {
		this.poutcome = poutcome;
	}

	@JsonProperty("outcome")
	public String getOutcome() {
		return outcome;
	}

	@JsonProperty("outcome")
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
}