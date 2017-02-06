import java.io.*;
import java.util.*;

// This class should be run using jdk1.5.0, -Xms600M, -Xmx600M
//
// C:\j2sdk1.4.2_15\bin\java.exe -Xms600M -Xmx600M aprimeperstudentC option

// THIS CLASS WILL GIVE UNPREDICTABLE RESULTS IF YOU GIVE IT DATA
// WITH THE LETTER E (FOR EXPONENTIAL) IN IT!
// MAKE SURE INPUT IS FORMATTED AS NUMBER NOT SCIENTIFIC
// RSB 16 JAN 2010

public class AprimeperstudentC 
{

    static int inputmax = 0;
 
    static String inputfile = "C:\\Documents and Settings\\falcor\\My Documents\\Science Assistments\\Publications\\UMUAI 2010\\AnalysisBayesNets\\outFromProto.txt";

    static final int NUMACTIONS = 2000; // 156,000 for algebra. 35,000 for geometry, 246798 for midsch, 5300 for geogh, 60000 for circles, 564 for Leicester-science-assistments
    static final int MODELS = 1; // 3 for fortest, 3 for cvnh, 2 for algebra, 3 for geometry, 13 for geogh, 2 for circles, 2 for SciAssist
    static final int STUDENTS = 1; // 59 for algebra, 88 for geometry, 233 for midsch, 147 for Leicester-science-assistments

    double action_corrects[] = new double[NUMACTIONS]; 
    double model_lnminusone_values[][] = new double[MODELS][NUMACTIONS]; // ln-1

    int student_begins[] = new int[STUDENTS]; 
    int student_ends[] = new int[STUDENTS];

    double aprimes[][] = new double[STUDENTS][2];
    double SEs[][] = new double[STUDENTS][2];
    double SIZEs[][] = new double[STUDENTS][2];

    String model_names[] = new String[MODELS];

    boolean weighted_z = true; // true is weighted Z (Whitlock, 2005); false is Stouffer's test

    int numrecords_ = -1;

    boolean cautious_se = true; // if true, disqualify se for students with under 3 observations, or no tp or no fp

    public AprimeperstudentC(){
    }

    public void a_prime_general (String extinput1, String extinput2){
	input_student_records(inputfile);
	a_prime_driver_labeled(extinput1, extinput2);
    }

    public void a_prime_driver_labeled(String set1, String set2){
	int models_encountered = 0;

	for (int i=0;i<MODELS;i++){
		if (model_names[i].equals(set1)){ 		
		double model_values[] = new double[NUMACTIONS];
		for (int j=0;j<NUMACTIONS;j++)
		    model_values[j]=model_lnminusone_values[i][j];	    
		a_prime(action_corrects, model_values, model_names[i],1);
		models_encountered++;
	    }		
	    if (model_names[i].equals(set2)){ 		
		double model_values[] = new double[NUMACTIONS];
		for (int j=0;j<NUMACTIONS;j++)
		    model_values[j]=model_lnminusone_values[i][j];	    
		a_prime(action_corrects, model_values, model_names[i],2);
		models_encountered++;
	    }	
	}
	if (models_encountered<2)
	    System.out.println("Model not found!");
	Z_TEST(set1,set2);
    }
 
    public String lookup_p(double Z){
	double abs_z = java.lang.Math.abs(Z);
	if (abs_z>3.3)
	    return "<0.001";
	if (abs_z>2.58)
	    return "<0.01";
	if (abs_z>2.44)
	    return "0.01";
	if (abs_z>2.25)
	    return "0.02";
	if (abs_z>2.11)
	    return "0.03";
	if (abs_z>2.01)
	    return "0.04";
	if (abs_z>1.92)
	    return "0.05";
	if (abs_z>1.85)
	    return "0.06";
	if (abs_z>1.78)
	    return "0.07";
	if (abs_z>1.72)
	    return "0.08";
	if (abs_z>1.67)
	    return "0.09";
	if (abs_z>1.62)
	    return "0.10";
	if (abs_z>1.57)
	    return "0.11";
	if (abs_z>1.53)
	    return "0.12";
	return ">0.12";
    }

    public void Z_TEST(String set1, String set2){
	System.out.println("Student\t"+set1+"\t"+set2+"\tZ\tp"); 

	double finalZ = 0; double count = 0;
	double aprimes_1 = 0; double aprimes_2 = 0; double total_df = 0;
	for (int stu=0;stu<STUDENTS;stu++){
	    if((aprimes[stu][0]>-1)&&(aprimes[stu][1]>-1)){
		System.out.print(stu);
		System.out.print("\t");
		System.out.print(aprimes[stu][0]);
		aprimes_1 = aprimes_1 + aprimes[stu][0];
		System.out.print(" (");
		System.out.print(SEs[stu][0]);
		System.out.print(")\t");
		System.out.print(aprimes[stu][1]);
		aprimes_2 = aprimes_2 + aprimes[stu][1];
		System.out.print(" (");
		System.out.print(SEs[stu][1]);
		System.out.print(")\t");
		double Z = (aprimes[stu][1] - aprimes[stu][0])/java.lang.Math.sqrt(SEs[stu][0]*SEs[stu][0] + SEs[stu][1]*SEs[stu][1]);
		System.out.print(new Double(Z));
		System.out.print("\t");
		System.out.println(lookup_p(Z));
		if (weighted_z){
		    finalZ = finalZ + Z * (SIZEs[stu][0] - 2);
		    total_df = total_df + ((SIZEs[stu][0] - 2)*(SIZEs[stu][0] - 2));
		}
		else
		    finalZ = finalZ + Z;
		count++;
	    }
	}  
	
	System.out.println(); System.out.println();
	
	System.out.print("Average A prime (" + set1 + "):");
	double avg_1 = aprimes_1 / count;
	System.out.println(avg_1);
	
	System.out.print("Average A prime (" + set2 + "):");
	double avg_2 = aprimes_2 / count;
	System.out.println(avg_2);
	
	System.out.print("Meta Z");
	if (weighted_z)
	    System.out.print(" (Weighted Z):"); 
	else	    
	    System.out.print(" (Stouffer's method):"); 

	double metaz = 0.0;

	if (weighted_z)
	    metaz = finalZ/java.lang.Math.sqrt(total_df);
	else
	    metaz = finalZ/java.lang.Math.sqrt(count);
	System.out.print(metaz);
	System.out.print("\tp:");
	System.out.println(lookup_p(metaz));
	

    }

    // note that the variable names are wrong here -- predicted gets treated as actual, and vice-versa. Eeep! -- RSB, 16 Jan 2010
    public double a_prime (double predicted[], double actual[], String population, int whichset){ 
    	
    	System.err.println("A predicted value [0] to make sure continuous vals seen: " + predicted[0]);

	for (int stu=0;stu<student_ends.length;stu++){	

	int total_tp = 0;
	int total_fp = 0;
	int cur_tp = 0;
	int cur_fp = 0;
	double actuall[] = new double[actual.length];

	double minvalue = 1000000.0;

	for (int i=student_begins[stu];i<=student_ends[stu];i++)
	    actuall[i] = actual[i];

	for (int i=student_begins[stu];i<=student_ends[stu];i++){
	    
		if (predicted[i]>0)
		    total_tp+=1;
		else
		    if (predicted[i]!=-1)
			total_fp+=1;	    
		if (actual[i]<minvalue)
		    minvalue=actual[i];

	}
	
	/**	if (stu ==1){	
	    System.out.println(student_begins[stu]);
	    System.out.println(student_ends[stu]);
	    System.out.println(total_tp);	       
	    System.out.println(total_fp);
	    
	    }*/

	boolean still_more_to_add = true; int loc = -1;
	double segarea = 0; double curval; int count;
	double pctX=-1; double pctY=-1;
	double oldpctX=-1; double oldpctY=-1;

	double lastpctX = -1; double lastpctY = -1;

	int firsttime = 0;

	while (still_more_to_add){

	    curval = -1;
	    loc = -1;
	    for (int i=student_begins[stu];i<=student_ends[stu];i++){
		if (actuall[i]>curval){
		    curval=actuall[i];
		    loc=i;
		}
	    }
	    if (curval == -1)
		still_more_to_add=false;
	    else{		
	
		if ((curval == 0)||(curval==minvalue)){
		// at the final clump of predicted 0 gamers
		/**if (stu == 1){
		    System.out.println("atend");
		    System.out.println(pctX);
		    System.out.println(pctY);
		    System.out.println("atende");
		    }*/
		still_more_to_add=false;
		
		if ((oldpctX == lastpctX)&&(oldpctY == lastpctY)){
		    // this is a hack but seems to produce the correct results... -- rsb
		    oldpctX = pctX;
		    oldpctY = pctY;
		}
		pctX = 1;
		pctY = 1;

		//segarea += ((pctY - oldpctY) * (pctX - oldpctX) * 0.5) + ((pctX - oldpctX) * oldpctY);
	    }else
		    if (predicted[loc]>0){
			oldpctX = (new Double(cur_fp).doubleValue()) / (new Double(total_fp).doubleValue());
			pctX = oldpctX;		    
			oldpctY = (new Double(cur_tp).doubleValue()) / (new Double(total_tp).doubleValue());
			cur_tp+=1;
			pctY = (new Double(cur_tp).doubleValue()) / (new Double(total_tp).doubleValue());
			
		    } else
			if (predicted[loc]!=-1){
			    oldpctX = (new Double(cur_fp).doubleValue()) / (new Double(total_fp).doubleValue());
			    cur_fp+=1;
			    pctX = (new Double(cur_fp).doubleValue()) / (new Double(total_fp).doubleValue());		    		   
			    oldpctY = (new Double(cur_tp).doubleValue()) / (new Double(total_tp).doubleValue());
			    pctY = oldpctY;
			}
		    if (curval < 1)
			firsttime++;
		    if (firsttime == 1){
			oldpctX = 0;
			oldpctY = 0;
		    }
	    }

		//	System.out.println((new Double(pctY)).toString()+","+(new Double(pctX)).toString());	  	    

	    /**if ((curval<1)&&(pctX<1)){
	    segarea += ((pctY - oldpctY) * (pctX - oldpctX) * 0.5) + ((pctX - oldpctX) * oldpctY);
	    lastpctX = oldpctX; lastpctY = oldpctY;
	    //System.out.println((new Double(pctY)).toString()+ "," + (new Double(pctX)).toString()+ "," + (new Double(oldpctY)).toString()+ "," + (new Double(oldpctX)).toString());
      
	    }*/
	    
	    if ((curval<1)&&((pctY<1)||(pctX<1))){
	    segarea += ((pctY - oldpctY) * (pctX - oldpctX) * 0.5) + ((pctX - oldpctX) * oldpctY);
	    lastpctX = oldpctX; lastpctY = oldpctY;
	    //System.out.println((new Double(pctY)).toString()+ "," + (new Double(pctX)).toString()+ "," + (new Double(oldpctY)).toString()+ "," + (new Double(oldpctX)).toString());
	    /**if (stu == 0){
		System.out.println(curval);
		System.out.println((new Double(oldpctX)).toString()+ "," + (new Double(oldpctY)).toString());
		System.out.println((new Double(pctX)).toString()+ "," + (new Double(pctY)).toString());
		System.out.println(segarea);
		System.out.println();
		}*/
	    }

	    if ((curval<1)&&(pctY == pctX)&&(pctY == 1)){

		still_more_to_add=false;
		segarea += ((pctY - oldpctY) * (pctX - oldpctX) * 0.5) + ((pctX - oldpctX) * oldpctY);
		lastpctX = oldpctX; lastpctY = oldpctY;
	    }
	    


	    if (loc!=-1)
		actuall[loc] = -2;
	}      
	 
	/**	if (stu == 1){
	    System.out.print("A:");
	System.out.println(segarea);
	System.out.println(lastpctX);
	System.out.println(lastpctY);
	    System.out.print("oldpctX:");
	System.out.println(oldpctX);
	System.out.println(oldpctY);
	    System.out.print("pctX:");
	System.out.println(pctX);
	System.out.println(pctY);

	//	System.out.println(cur_tp);
	//	System.out.println(cur_fp);
	}*/

	if (whichset == 1){
	    if (standard_error(segarea,total_tp,total_fp)>-1){
		aprimes[stu][0] = segarea;
		SEs[stu][0] = standard_error(segarea,total_tp,total_fp);		
		SIZEs[stu][0] = total_tp + total_fp;
	    }
	    else{
		aprimes[stu][0] = -1;
		SEs[stu][0] = -1;
	    }
	}
	if (whichset == 2){
	    if (standard_error(segarea,total_tp,total_fp)>-1){
		aprimes[stu][1] = segarea;
		SEs[stu][1] = standard_error(segarea,total_tp,total_fp);		
		SIZEs[stu][1] = total_tp + total_fp;
	    }
	    else{
		aprimes[stu][1] = -1;
		SEs[stu][1] = -1;
	    }
	}
	
	if (stu == 1)
	    System.out.println();

	/**	System.out.print(population);
	System.out.print("\t");
	System.out.print(stu);
	System.out.print("\t");
	System.out.print(new Double(segarea));
	System.out.print("\t");
	System.out.println(standard_error(segarea,total_tp,total_fp));*/	
	
	if ((segarea>1)||(segarea<0)){
	    System.out.println("INVALID A-PRIME VALUE OBTAINED!");
	    System.out.println(segarea);
	    System.out.println(stu);
	    System.out.println(total_tp);
	    System.out.println(total_fp);
	}

	}
	return -1;
    } 

    public double standard_error(double aprime, int total_tp, int total_fp){
	if (total_tp+total_fp<2)
	    return -1.0;
	if ((total_tp==0)||(total_fp==0)||(total_fp+total_tp<3)||(aprime==0))
	    if (cautious_se)
		return -1.0; // not enough data here to validly calculate
	    else
		return 0.0;
	
	double aoneminusa = aprime * (1-aprime);
	double naminus1 = total_tp-1;
	double q1 = aprime/ (2-aprime);
	double a2 = aprime * aprime;
	double q1minusa2 = q1-a2;
	double natimes = naminus1 * q1minusa2;
	double nnminus1 = total_fp-1;
	double q2 = (2*a2)/(1+aprime);
	double q2minusa2 = q2-a2;
	double nntimes = nnminus1 * q2minusa2;
	double top = aoneminusa + natimes + nntimes;
	double nann = naminus1 * nnminus1;
	if (nann==0.0)
	     if (cautious_se)
		return -1.0; // not enough data here to validly calculate
	    else
		return 0.0;
	double inside = top/nann;
	double se = java.lang.Math.sqrt(inside);
	
	return se;
    }

    public void input_student_records (String file){

	StreamTokenizer isr_;

	int sofar = -1;int i = -1; int k=0;
	try{
	    isr_ = new StreamTokenizer(new FileReader(file));

	    int tt = -6948; int j = 0;
	    String name = "henryanonymous";
	    String nuname = "";
	    boolean quitnow = false;

	    //first load in header row
	    while ((tt != StreamTokenizer.TT_WORD)&&(tt != StreamTokenizer.TT_EOF))
		tt = isr_.nextToken();
	    while ((tt != StreamTokenizer.TT_WORD)&&(tt != StreamTokenizer.TT_EOF))
		tt = isr_.nextToken();

	    int model = 0;
	    while (!isr_.sval.equals("eol"))
	    {
	    	tt = isr_.nextToken();
	    	if (tt != StreamTokenizer.TT_WORD)
	    		throw new RuntimeException("Header error");	

	    	if ((!isr_.sval.equals("eol"))&&(!isr_.sval.equals("right"))&&(!isr_.sval.equals("student"))&&(!isr_.sval.equals("NuCode-b")))
	    	{
	    		System.out.println("Model found: " + isr_.sval);
	    		model_names[model++] = isr_.sval;
	    	}
	    }	    
	    tt = isr_.nextToken();

	    //now load in data
	    while (!quitnow){
		while ((tt != StreamTokenizer.TT_WORD)&&(tt != StreamTokenizer.TT_EOF))
		{
		    tt = isr_.nextToken();
		    //System.err.println(isr_.sval);
		}
		if (tt == StreamTokenizer.TT_EOF)
		    quitnow=true;
		else{		    
		    sofar++;

		    nuname = isr_.sval;
		    if (!nuname.equals(name)){
			name = nuname;
			//System.out.println("Name: " + name);
			if (nuname.equals("EOL"))		    
			    throw new RuntimeException("Alignment error!");
			i++;
			//	if (i==34)
			student_begins[i]=sofar;
			if (sofar != 0)
			    student_ends[i-1]=sofar-1;
			
		    }
		    tt = isr_.nextToken();
		    while ((tt != StreamTokenizer.TT_EOL)&&(tt != StreamTokenizer.TT_EOF)&&(!((tt== StreamTokenizer.TT_WORD)&&(isr_.sval.equalsIgnoreCase("EOL"))))){
			if (tt == StreamTokenizer.TT_NUMBER){
			    if (j==0)
				action_corrects[k]=isr_.nval;
			    if (j>0)
				model_lnminusone_values[j-1][k]=isr_.nval;
			    
			    j++;     
			}
			//System.out.println((new Integer(k)).toString() + "," + (new Integer(j)).toString());
			tt = isr_.nextToken();
		    }
		    tt = isr_.nextToken();

		    j = 0; k++;
		    if (tt == StreamTokenizer.TT_EOF)
			quitnow = true;
		}	    
	    }}
	catch (Exception fnfe){
	    fnfe.printStackTrace();
	}
	numrecords_ = sofar;
	student_ends[i]=sofar;	

    }

    public static void main (String args[])
    {
    	AprimeperstudentC h = new AprimeperstudentC();

    	if (args.length<3)
    	{
			System.err.println("Usage: inputfile model1 model2");
			System.err.println("inputfile: data generated from Proto.java");
			System.err.println("model1/model2: column headers in inputfile (e.g. bounded) representing a prediction made by a named BKT model.");
			return;

    	}
    	
    	inputfile = args[0];
	    h.a_prime_general(args[1], args[2]);
	
    }

    
}
