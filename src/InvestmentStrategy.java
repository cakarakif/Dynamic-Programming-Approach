import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InvestmentStrategy {

	public static void main(String[] args) throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		int investNumber = 0, investYear = 0;
		double investMoney = 0;
		int[] investId;

		System.out.print("Number of the investments: ");
		investNumber = scanner.nextInt();
		System.out.print("Enter the amount of money you will invest: ");
		investMoney = scanner.nextDouble();
		System.out.print("How many years will you invest: ");
		investYear = scanner.nextInt();

		investId = new int[investNumber];
		for (int i = 0; i < investNumber; i++) {
			System.out.print("Enter the id of the " + (i + 1) + ". investment: ");
			investId[i] = scanner.nextInt();
		}

		///////////// Kullanýcadan girdiler alýndý.
		//////////////////////////////////////////
		
		Double[][] investList = new Double[investNumber + 2][investYear + 1];
		// +1 invesment id'si için tutuldu.
		// +2 vergiler(f1 ve f2) tutuldu.

		scanner = new Scanner(new File(
				"C://Users//akifc//Desktop//WorkSpace//EclipseWorkspace//CME2204-Assignment2//src//input.txt"));
		String line;
		String[] arr;
		int flag = 0;// gereksiz döngüler engellendi.
		scanner.nextLine();// ilk satýr atlatýldý.
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			// istenilen investment verileri alýndý.
			if (flag != investNumber) {
				arr = line.split("#"); // splits a string by '#'
				for (int i = 0; i < investId.length; i++) {
					if (investId[i] == Integer.parseInt(arr[0])) {
						flag++;
						for (int j = 0; j < investYear + 1; j++) {
							investList[i][j] = Double.parseDouble(arr[j]);
						}
					}
				}
			}
			// vergiler(f1 ve f2) ayný arrayde tutuldu.
			else if (line.substring(0, 2).equals("f1") || line.substring(0, 2).equals("f2")) {
				arr = line.split("#"); // splits a string by '#'
				if (investList[investNumber][0] == null) {
					investList[investNumber][0] = 1.0;
					for (int j = 1; j < investYear; j++)
						investList[investNumber][j] = Double.parseDouble(arr[j]);
				} else {
					investList[investNumber + 1][0] = 2.0;
					for (int j = 1; j < investYear; j++)
						investList[investNumber + 1][j] = Double.parseDouble(arr[j]);
				}

			}

		}

		/// Girilen inputa göre,kullanýlacak olan veriler txt'den arraye alýndý.
		///////////////////////////////////////////////////////////////////////
		
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
		System.out.print("Greedy Approach:");

		int calcuteNumber = 0;// dpTable için satýr sayýsý hesaplandý.
		for (int i = 0; i < investYear; i++) {
			calcuteNumber += Math.pow(investNumber, i);
		}

		int counter = 0, permit = 0, curTime = 2, location = 0, dot = 0,pot=0;
		int[] greedy = new int[investYear];//hem dp hem greedy hesaplandý.
		double profitable = Double.MIN_VALUE,compare=Double.MIN_VALUE, temp = 0.0;
		Double[][] dpTable = new Double[calcuteNumber + 1][investNumber + 1];
		calcuteNumber = (int) Math.pow(investNumber, curTime - 1) + 1;

		for (int i = 0; i < dpTable.length; i++) {
			if (i != 0 && i != 1) {
				counter++;
				permit++;
			}
			if (permit == investNumber + 1) {
				counter--;
				permit = 1;
				if (i > calcuteNumber) {
					curTime++;
					calcuteNumber += (int) Math.pow(investNumber, curTime - 1);
				}
			}
			for (int j = 0; j < dpTable[i].length; j++) {
				if (i == 0 || (permit == 0 && j == 0))
					dpTable[i][j] = 0.0;
				else if (i == 1){
					dpTable[i][j] = (investList[j - 1][i] * investMoney) / 100 + investMoney;
					if(compare < dpTable[i][j]){
						greedy[0]=i;
						pot=j;
						compare=dpTable[i][j];
						}
					if(j==investNumber)
						System.out.print(" - "+investId[pot - 1]);
				}
				else if (j == 0 && permit != 0) // Hesaplama sonunda geriyi
												// takip etmek için konuldu.
					dpTable[i][j] = (double) i - counter + Double.parseDouble("0." + permit);
				else {
					temp = (investList[j - 1][curTime] * dpTable[i - counter][permit]) / 100 + dpTable[i - counter][permit];

					if (j == permit)
						dpTable[i][j] = temp - (investList[investNumber][curTime - 1] * temp) / 100;
					else
						dpTable[i][j] = temp - (investList[investNumber + 1][curTime - 1] * temp) / 100;
					///Her bir adým için gerekli hesaplamalar yapýldý.
					if (profitable < dpTable[i][j]) {
						profitable = dpTable[i][j];
						location = i;
						dot = j;
					}//en yüksek kar tutuldu.
					if(1+greedy[curTime-2]*investNumber==i ){
						if(compare < dpTable[i][j]){
							greedy[curTime-1]=i;
							compare=dpTable[i][j];
							pot=j;
						}
						if(j==investNumber)
							System.out.print(" - "+investId[pot - 1]);
						if(i==dpTable.length-1 && j==investNumber)
							System.out.print(" -=-> "+String.format("%.03f", compare));
					}
				}

			}
		}
		
		///Tüm adýmlar için gerekli iþlemler yapýlýp, dolduruldu.
		///////////////////////////////////////////////////////////////////////
		
		String mediator = "";
		int result = 0;
		int [] options=new int[investYear];
		double best=dpTable[location][dot];
		
		System.out.println();
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
		System.out.println("Optimal Approach:");
		for (int i = 0; i < investYear; i++) {// Daha sonra diðerleri takip
												// edilerek yazdýrýldý.
			if (i == 0){// Ýþaretlenen ekrana yazdýrýldý.
				System.out.println("The best investment option for the " + (investYear - i) + ". year is "+ investId[dot - 1] + ". investment -> your money will be " +String.format("%.03f", dpTable[location][dot]) );
				options[i]=investId[dot - 1];
			}else {
				mediator = Double.valueOf(dpTable[location][0]).toString();
				mediator = mediator.substring(mediator.indexOf('.') + 1);
				result = Integer.valueOf(mediator);

				location = dpTable[location][0].intValue();
				System.out.println("The best investment option for the " + (investYear - i) + ". year is "+ investId[result - 1] + ". investment -> your money will be " + String.format("%.03f", dpTable[location][result]));
				options[i]=investId[result - 1];
			}

		}
		System.out.println("-----------------------------------");
		System.out.println("Total investment after "+investYear+" years is :"+String.format("%.03f", best));
		System.out.print("-Best investment options are:");
		for (int i = options.length-1; i >= 0; i--) {
			System.out.print("-"+options[i]);
		}
		System.out.println();
		System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------");
		///Ekrana yazdýrma iþlemi yapýldý.
		//////////////////////////////////
	}

}
