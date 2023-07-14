//6c11 玉井優乃

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class DietApp {

	public static void main(String[] args) throws Exception{

		Scanner sc=new Scanner(System.in);
		ArrayList<Data> list;
		File dataFile=new File("data.csv");
		File routeFile=new File("route.csv");
		DateTimeFormatter dtf=DateTimeFormatter.ofPattern("yyyyMMdd");
		float[] route;


		if(dataFile.exists()){
			list=loadDataFile(dataFile);
		}else{
			list=new ArrayList<>();
		}
		if(routeFile.exists()){
			route=loadRouteFile(routeFile);
		}else{
			route=new float[2];
		}

		float first=route[0];
		float goal=route[1];

		System.out.println("＊＊＊Diet Manager＊＊＊");
		//↓初回起動時
		if(routeFile.exists()==false){
			try{
				System.out.print("現在の体重を入力してください(kg) >>");
				first=sc.nextInt();
				System.out.print("目標体重を入力してください(kg) >>");
				goal=sc.nextInt();
			}catch(Exception e){
				System.out.println("数字以外が入力されました");
				System.out.println("操作をやり直してください");
				sc.next();
			}

		}

		//目標まであと○○㎏！と表示
		float difference=first-goal;
		System.out.println("・・・");
		System.out.println("目標まであと"+difference+"kg！");
		while(true){
			//メニュー
			System.out.printf("%n1:入力 2:目標変更 3:終了%n");
			System.out.print("操作を入力してください >>");
			try{
				int select=sc.nextInt();
				if(select==3){
					//終了
					System.out.println("アプリを終了します");
					sortList(list);
					sc.close();
					saveDataFile(dataFile,list);
					saveRouteFile(routeFile,first,goal);
					return;
				}else
				if(select==1){
					//入力
					while(true){
						System.out.print("1:今日の分 2:入れ忘れ >>");
						try{
							int slc=sc.nextInt();
							if(slc==1){
								//今日
								while(true){
									System.out.print("体重を入力してください(kg) >>");
									try{
										float weight=sc.nextInt();
										LocalDate day=LocalDate.now();
										Data data=new Data(day,weight);
										list.add(data);
										sortList(list);
										transition(list,first,goal);
										break;
									}catch(Exception e){
										System.out.println("数字以外が入力されました");
										System.out.println("操作をやり直してください");
										sc.next();
									}
								}break;

							}else
							if(slc==2){
								//過去
								while(true){
									System.out.println("日付を8桁の数字で入力してください");
									System.out.print("例)2020年5月10日→20200510 >>");
									try{
										String indate=sc.next();
										if(indate.length()==8){
											LocalDate day=LocalDate.parse(indate,dtf);
											while(true){
												System.out.print("体重を入力してください(kg) >>");
												try{
													float weight=sc.nextInt();
													Data data=new Data(day,weight);
													list.add(data);
													sortList(list);
													transition(list,first,goal);
													break;
												}catch(Exception e){
													System.out.println("数字以外が入力されました");
													System.out.println("操作をやり直してください");
													sc.next();
												}
											}
										}else{
											System.out.println("桁数が異なっています");
											System.out.println("入力形式を確認してください");
											continue;
										}
									}catch(Exception e){
										System.out.println("数字以外が入力されました");
										System.out.println("操作をやり直してください");
										sc.next();
									}break;
								}break;

							}else{
								System.out.println("範囲外の数字が入力されました");
								System.out.println("操作をやり直してください");
								continue;
							}

						}catch(Exception e){
							System.out.println("数字以外が入力されました");
							System.out.println("操作をやり直してください");
							sc.next();
						}
					}

				}else
				if(select==2){
					//目標変更
					try{
						System.out.println("現在の目標 "+goal+"kg");
						System.out.print("新しい目標体重を入力してください(kg) >>");
						float changeGoal=sc.nextInt();
						goal=changeGoal;
						System.out.println("目標を"+goal+"kgに変更しました");
					}catch(Exception e){
						System.out.println("数字以外が入力されました");
						System.out.println("操作をやり直してください");
						sc.next();
					}

				}else{
					System.out.println("範囲外の数字が入力されました");
					System.out.println("操作をやり直してください");
					continue;
				}

			}catch(Exception e){
				System.out.println("数字以外が入力されました");
				System.out.println("操作をやり直してください");
				sc.next();
			}

		}
	}


	//体重遷移と評価
	static void transition(ArrayList<Data> list,float first,float goal){
		int n=list.size();
		float newest=list.get(n-1).weight;
		float last=0;

		if(newest<goal){
			System.out.println("おめでとう！目標達成！！");
		}else{
			if(n>1){
				last=list.get(n-2).weight;
				System.out.println("前回から"+(newest-last)+"kg");
			}
			System.out.println("スタートから"+(newest-first)+"kg");
			System.out.println("目標まで あと"+(goal-newest)+"kg！");
		}

		if(n>1){
			if((newest-last)<0){
				evaluation();
			}
		}else
		if((newest-first)<0){
			evaluation();
		}

	}

	//評価
	static void evaluation(){
		Random r=new Random();
		String[] support={"頑張ってるね！この調子！","順調順調！","いい感じだよ！"};
		int n=support.length;

		int random=r.nextInt(n);
		System.out.println(support[random]);
	}

	//日付順でsort
	static void sortList(ArrayList<Data> list){
		for(int i=0;i<list.size();i++){
			for(int j=0;j<list.size();j++){
				if(list.get(i).day.isBefore(list.get(j).day)){
					Data temp=list.get(i);
					list.set(i, list.get(j));
					list.set(j,temp);
				}
			}
		}
	}

	//保存
	//「yyyy-mm-dd,○○.○○(日付,kg)」の形式
	static void saveDataFile(File dataFile,ArrayList<Data> list)throws Exception{
		FileOutputStream dfos=new FileOutputStream(dataFile);
		OutputStreamWriter dosw=new OutputStreamWriter(dfos,"UTF-8");
		BufferedWriter dbw=new BufferedWriter(dosw);

		for(Data r:list){
			dbw.write(r.toCSV());
			dbw.newLine();
		}
		dbw.close();
	}

	//「○○.○○,○○.○○(kg,kg)」の形式
	static void saveRouteFile(File routeFile,float first,float goal)throws Exception{
		FileOutputStream rfos=new FileOutputStream(routeFile);
		OutputStreamWriter rosw=new OutputStreamWriter(rfos,"UTF-8");
		BufferedWriter rbw=new BufferedWriter(rosw);

		String s="";
		s += first+","+goal;

		rbw.write(s);
		rbw.close();
	}

	//読み込み
	static ArrayList<Data> loadDataFile(File dataFile)throws Exception{
		ArrayList<Data> list=new ArrayList<>();
		FileInputStream dfis=new FileInputStream(dataFile);
		InputStreamReader disr=new InputStreamReader(dfis,"UTF-8");
		BufferedReader dbr=new BufferedReader(disr);

		String line;
		while((line=dbr.readLine())!=null){
			String[] values1=line.split(",");
			String tempDay=values1[0];
			LocalDate day=LocalDate.parse(tempDay);
			String tempWeight=values1[1];
			float weight=Float.parseFloat(tempWeight);
			Data data=new Data(day,weight);
			list.add(data);
		}
		dbr.close();
		return list;
	}

	static float[] loadRouteFile(File routeFile)throws Exception{
		float[] route=new float[2];
		FileInputStream rfis=new FileInputStream(routeFile);
		InputStreamReader risr=new InputStreamReader(rfis,"UTF-8");
		BufferedReader rbr=new BufferedReader(risr);

		String line;
		while((line=rbr.readLine())!=null){
			String[] values2=line.split(",");
			route[0]=Float.parseFloat(values2[0]);
			route[1]=Float.parseFloat(values2[1]);
		}
		rbr.close();
		return route;
	}


}
