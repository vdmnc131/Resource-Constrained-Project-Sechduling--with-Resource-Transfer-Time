import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.Random;
 class job {
    int id;
    int duration;
    int succ;
    int[] s_list;
    Vector<Integer> p_list = new Vector<Integer>();
    int pred;
    
}
public class schedule {

	public static void main(String[] args) throws IOException{
		BufferedReader BR = null;
		try {
			BR = new BufferedReader(new FileReader("test-1.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String qline = BR.readLine();
        int job_count = 32;
        int i=0;
        // int [] FT = new int[33];                       // finishing time of all activities
        // for(int ss=1;ss<FT.length;ss++){
        //     FT[ss]=0;
        // }  
        Vector<job> j_list = new Vector<job>();
        
        while (qline != null && i<32) {
            String[] items = qline.split("	");
            
            job temp = new job();
            temp.id = Integer.parseInt(items[0]);
            int tem = Integer.parseInt(items[2]);
            temp.succ = tem;
            temp.s_list = new int[tem];
            
            for(int j=0; j<tem; j++){
            	
            	temp.s_list[j] = Integer.parseInt(items[3+j]);
            }
            j_list.add(temp);
            qline = BR.readLine();
            
            i++;
        }
        
        for(int a=0; a<j_list.size(); a++){
        	for(int b=0; b<j_list.get(a).succ; b++){
        		j_list.get(j_list.get(a).s_list[b]-1).p_list.add(a+1);
        		j_list.get(j_list.get(a).s_list[b]-1).pred++;
        	}
        }
        //qline = BR.readLine();
        i = 0;
        int[][] resources = new int[33][4];
        while (qline != null && i<32){
        	qline = BR.readLine();
        	//System.out.println(qline);
        	String[] items = qline.split("	");
        	int id = Integer.parseInt(items[0]);
            j_list.get(id-1).duration = Integer.parseInt(items[2]);
            resources[id][0] = Integer.parseInt(items[3]);
            resources[id][1] = Integer.parseInt(items[4]);
            resources[id][3] = Integer.parseInt(items[5]);
            resources[id][2] = Integer.parseInt(items[6]);
            i++;
            
        }
        
        qline = BR.readLine();
        i=0;
        
        Vector<int[][]> r = new Vector<int[][]>();
        int[][] r1 = new int[33][33];
        while(i<32){
        	qline = BR.readLine();
        	String[] items = qline.split("	");
        	for(int j=0; j<items.length-1; j++){
        		r1[i+1][j+1] = Integer.parseInt(items[j+1]);
        	}
        	i++;
        }
        r.add(r1);
        
        qline = BR.readLine();
        i=0;
        int[][] r2 = new int[33][33];
        while(i<32){
        	qline = BR.readLine();
        	String[] items = qline.split("	");
        	for(int j=0; j<items.length-1; j++){
        		r2[i+1][j+1] = Integer.parseInt(items[j+1]);
        	}
        	i++;
        }
        r.add(r2);
        qline = BR.readLine();
        i=0;
        int[][] r3 = new int[33][33];
        while(i<32){
        	qline = BR.readLine();
        	String[] items = qline.split("	");
        	for(int j=0; j<items.length-1; j++){
        		r3[i+1][j+1] = Integer.parseInt(items[j+1]);
        	}
        	i++;
        }
        
        r.add(r3);
        qline = BR.readLine();
        i=0;
        int[][] r4 = new int[33][33];
        while(i<32){
        	qline = BR.readLine();
        	String[] items = qline.split("	");
        	for(int j=0; j<items.length-1; j++){
        		r4[i+1][j+1] = Integer.parseInt(items[j+1]);
        	}
        	i++;
        }
        
        r.add(r4);
        BR.close();
        //end of reading data
        
        BufferedReader BR1 = null;
		try {
			BR1 = new BufferedReader(new FileReader("time.txt"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String line;
        int[] start_time = new int[32];
        int[] end_time = new int[32];
		for(int j=0; j<32; j++){
        	line = BR1.readLine();
        	String[] items = line.split(" ");
        	start_time[j] = Integer.parseInt(items[2]);
        	end_time[j] = Integer.parseInt(items[4]);
        	
        }
		
		// for(int a=0; a<j_list.size(); a++){
		// 	//System.out.print(j_list.get(a).id+" : ");
		// 	for(int b=0; b<j_list.get(a).p_list.size(); b++){
		// 		//System.out.print(j_list.get(a).p_list.get(b) +" ");
		// 	}
		// 	//System.out.println();
		// }
		//regret based random sampling
		//remove the first dummy entry in job list
        Vector<Vector<job>> G= new Vector<Vector<job>>() ;
        int [] fit =new int[100];
        for(int p=0;p<100;p++) {
            Vector<job> sig = new Vector<job>();
            sampling(j_list , sig, end_time);
            for(int a=0; a<j_list.size(); a++){
                for(int b=0; b<j_list.get(a).succ; b++){
                    j_list.get(j_list.get(a).s_list[b]-1).p_list.add(a+1);
                    j_list.get(j_list.get(a).s_list[b]-1).pred++;
                }
            }
            G.add(sig);
            fit[p]=fitness(sig, r, resources);
            System.out.println(fit[p]);
        }
		
	}

    static void sampling(Vector<job> j_list , Vector<job> sigma , int[] end_time ) {
        Vector<job> s = new Vector<job>();
        sigma.add(j_list.get(0));
        
        //int count = 0;
        for(int a=0; a<j_list.get(0).succ; a++){
            s.add(j_list.get(j_list.get(0).s_list[a] - 1));
        }

        //System.out.println("s.size() :");
        while(s.size()!=0){
            
            HashMap<Integer, Vector<Double>> p = new HashMap<Integer, Vector<Double>>();
            for(int k=0; k<s.size(); k++){
                Vector<Double> temp = new Vector<Double>();
                temp.add((double)end_time[s.get(k).id-1]);
                p.put(s.get(k).id, temp);
            }
            
            double max_p = 0;
            for(int k=0; k<s.size(); k++){
                if(p.get(s.get(k).id).get(0) > max_p) max_p = p.get(s.get(k).id).get(0);
            }
            
            double sum_r = 0;
            for(int k=0; k<s.size(); k++){
                p.get(s.get(k).id).add(1+max_p - p.get(s.get(k).id).get(0));
                sum_r += (1+max_p - p.get(s.get(k).id).get(0));
            }
            
            double cum_sum = 0;
            for(int k=0; k<s.size(); k++){
                double temp = p.get(s.get(k).id).get(1)/sum_r;
                cum_sum += temp;
                p.get(s.get(k).id).add(temp);
                p.get(s.get(k).id).add(cum_sum);
            }
            double ran = Math.random();
            int index = 0;
            for(int k=0; k<s.size(); k++){
                if(p.get(s.get(k).id).get(3) >= ran){
                    index = k;
                    break;
                }
            }

            sigma.add(s.get(index));
            
            for(int a=0; a<j_list.get(s.get(index).id-1).succ; a++){
                j_list.get(s.get(index).s_list[a]-1).pred--;
                if(!sigma.contains(j_list.get(s.get(index).s_list[a] - 1)) && !s.contains(j_list.get(s.get(index).s_list[a] - 1)))
                    {
                        if(j_list.get(s.get(index).s_list[a]-1).pred==0)
                            {s.add(j_list.get(s.get(index).s_list[a] - 1));
                                }
                    }
            }
            s.remove(index);
            
        }
        
       // System.out.println("size : " + sigma.size());
        for(int k=0; k<sigma.size(); k++){
           // System.out.println(sigma.get(k).id);
        }
    }



	// Now blindly follow the algorithm written in page 5.
	static int fitness(Vector<job> sigma , Vector<int[][]> r, int[][] resources) {
       
        int [] Q= new int [4];
        Q[0]=11; Q[1]=12; Q[2]=9; Q[3]=9;   // Q is the total resources as in this case it is 4 and indexing starts from zero.
        int[] D = new int[33];
        D[1]=1;                                        // those activities which have been scheduled indexing starts from 1.
        int [] FT = new int[33];                       // finishing time of all activities
        for(int i=1;i<FT.length;i++){
            FT[i]=0;
        } 
        int[][] v= new int[33][4];              // similar v,u as defined in paper and 33 wala 1 se shuhru hota hai and resources wala 0 se shuru hota hai.
        int [][][] u = new int [33][33][4];
        /// initialize wali list bachi hai

        for(int i=1;i<u.length;i++) {
            for(int j=1;j<u[1].length;j++) {
                for(int k=0;k<u[0][0].length;k++)
                    {
                        u[i][j][k]=0;                      // initialisation a bit sifferent from paper but I dont think this would make any difference .
                    }										// basically I have initialised every value to  be zero but paper me sab ko zero hi bola hai but baki ka kuch nhi bola hai

            }
        }
        for(int i=1;i<v.length;i++) {
            for(int l=0;l<v[0].length;l++) {
                if(i==1) {
                    v[i][l]=Q[l];
                }
                else {                              // initialisation same as paper
                    v[i][l]=0;
                }
            }

        }
       
        for(int lambda=2;lambda<=32;lambda++) {
            
           
            int j=sigma.get(lambda-1).id;                        // same as paper 

            boolean temp=false;
            int t=max_time(lambda ,FT, sigma);  



               // finiding the maximum finishing time of all the predecessors of activity j.
			while(!temp) {
               // System.out.println("AA");
                int [][][] Z = new int [33][4][33];
                int [][][] tau = new int [33][4][33];
                for(int d=1;d<33;d++) {
                    for(int q=0;q<4;q++) {
                        for(int kq=1;kq<33;kq++)
                            {
                                Z[d][q][kq]=0;  
                                tau[d][q][kq]=0;                    // initialisation a bit sifferent from paper but I dont think this would make any difference .
                            }                                       // basically I have initialised every value to  be zero but paper me sab ko zero hi bola hai but baki ka kuch nhi bola hai

                    }
                } 
                for(int k=0;k<4;k++) {
                    // Vector<Integer> Z= new Vector<Integer>();           // all z will be added in Z1
                    for(int l=1;l<=lambda-1;l++) {
                        int i = sigma.get(l-1).id;
                        // Vector<judwaa> tau= new Vector<judwaa>();    // all tau will be added in tau1 btw judwaa is a data type which stores two ids as it stores two index i, m for every k.
                        if(v[i][k]>0 && FT[i]+r.get(k)[i][j]<=t) 
                            Z[j][k][i]=1;
                        for(int e=1;e<=lambda-1;e++) {
                            int m= sigma.get(e-1).id;
                            // System.out.println(FT[m]-sigma.get(lambda-1).duration- sigma.get(e-1).duration-r.get(k)[j][m]-t);
                            // System.out.print("  goo ");
                            if( u[i][m][k]>0 && (FT[i]+r.get(k)[i][j]<=t) && (FT[m]-sigma.get(lambda-1).duration- sigma.get(e-1).duration-r.get(k)[j][m]>=t))  {
                                tau[j][k][i]= m;
                            }
                        }
                    }
                }

                boolean temp3 =true;
                int [] S= new int [4]; S[0]=0; S[1]=0;S[2]=0;S[3]=0;
                for(int k=0;k<4;k++) {
                    for(int vv=1; vv<33; vv++) {
                        if(Z[j][k][vv]==1)
                            S[k]+=v[vv][k];
                        if(tau[j][k][vv]!=0)
                            S[k]+=u[vv][tau[j][k][vv]][k]; 
                    }
                    if(S[k]<resources[j][k])
                        temp3=false;
                }
                
                //System.out.println("AfssfA");
                if(temp3) {
                   //System.out.println("AfqdqDqdfA");
                    for(int k=0;k<4;k++){
                       
                        //System.out.println(tau1.get(k).size());
                        if(v[j][k]<resources[j][k]) {
                            
                            for(int vv=1; vv<33; vv++) {
                                int x=0;
                                if(tau[j][k][vv]!=0) {
                                    if((resources[j][k]-v[j][k])<u[vv][tau[j][k][vv]][k])
                                        x=(resources[j][k]-v[j][k]);
                                    else
                                        x=u[vv][tau[j][k][vv]][k];

                                    u[vv][tau[j][k][vv]][k]=u[vv][tau[j][k][vv]][k]-x;
                                    u[vv][j][k]+=x; u[j][tau[j][k][vv]][k]+=x; v[j][k]+=x;
                                }
                            }
                        }
                        
                        if(v[j][k]<resources[j][k]) {
                            
                            for(int vv=1; vv<33; vv++) {  
                                int x=0;
                                if(Z[j][k][vv]==1) {
                                    if(v[vv][k] <(resources[j][k]-v[j][k]) )
                                        x= v[vv][k];
                                    else
                                        x=(resources[j][k]-v[j][k]);

                                    v[vv][k]=v[vv][k]-x;
                                    u[vv][j][k]+=x; v[j][k]+=x;
                                }
                            }
                           
                        }
                    }
                    D[lambda]=j;
                    FT[j]=t+sigma.get(lambda-1).duration;
                    temp= true;    
                                        // while loop se exit ki condition.
                }
                else{
                    t++;  
                   
                     //System.out.println("hh");
                }
            }    
                       
        }
        
        // for(int i=1;i<FT.length;i++) {
        //     //System.out.print("finishT ");
        //    // System.out.println(FT[i]);
        // }
        return(FT[32]);
       

    }
    static int max_time(int j ,int [] FT , Vector<job> sigma) {
    int temp=0;
    for(int i=0;i<sigma.get(j-1).p_list.size();i++) {
         //System.out.println(sigma.get(j-1).p_list.get(0));
        if(FT[sigma.get(j-1).p_list.get(i)]>temp) {
            temp=FT[sigma.get(j-1).p_list.get(i)];
        }
    }

    return(temp);   
    }


}
