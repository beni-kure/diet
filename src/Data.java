//6c11 玉井優乃

import java.time.LocalDate;

//日々のデータ管理

class Data {
	LocalDate day;
	float weight;

	Data(LocalDate day,float weight){
		this.day=day;
		this.weight=weight;
	}


	String toCSV(){
		return String.format("%s,%.2f",this.day,this.weight);
	}

}
