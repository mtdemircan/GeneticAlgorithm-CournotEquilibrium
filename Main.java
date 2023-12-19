import java.io.PrintWriter;
import java.util.*;

public class Main {
    public static final int n = 20,l=10,T=1000;
    public static final double pcross= 0.504,pmut=0.00404;

    public static void main(String[] args)
    {
        Random r = new Random();

        int[][] population=new int[n][l];
        for(int i=0;i<population.length;i++)
            for(int j=0;j<population[0].length;j++)
                population[i][j]=r.nextInt(2);


        int[][] nextPop;
        int[][] result;
        int[] Q=new int[T];
        int sum=0;

        double[] var=new double[T];
        double[] tmp;






        for(int i=0;i<T;i++){
            tmp=new double[20];
            sum=0;
            population=reproduce(population);
            result=crossover_mutation_election(population);


            for(int j=0;j<result.length;j++){
                sum+=getFitness(result[j]);

            }

            for(int j=0;j<result.length;j++)
                tmp[j]=getFitness(result[j]);
            Q[i]=sum;
            var[i]=variance(tmp,20);

        }
        try
        {
            PrintWriter pr = new PrintWriter("output.txt");

            for (int i=0; i<Q.length ; i++)
            {
                pr.println(Q[i]+","+i);
            }
            pr.close();
            PrintWriter pr2 = new PrintWriter("output2.txt");

            for (int i=0; i<Q.length ; i++)
            {
                pr2.println(var[i]+","+i);
            }
            pr2.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("No such file exists.");
        }



    }

    public static int[][] reproduce(int[][] pop){
        Random r=new Random();
        int totalfitness=0;
        int rand=0,tmp2=0;
        int[][] result=new int[n][l];
        for(int i=0;i<pop.length;i++){

            totalfitness+=getFitness(pop[i]);
        }
        for(int i=0;i<pop.length;i++){
            rand=r.nextInt(totalfitness);

            tmp2=0;
            for(int j=0;j<pop.length;j++){
                tmp2+=getFitness(pop[j]);

                if(tmp2>=rand){

                    for(int k=0;k<10;k++)
                        result[i][k]=pop[j][k];
                    break;
                }
            }
        }
        return result;
    }

    public static void displayPopulation(int[][] p){
        for(int i=0;i<p.length;i++){
            for(int j=0;j<10;j++){
                System.out.print(p[i][j]+" ");
            }
            System.out.println();
        }

    }

    public static int getFitness(int[] c){
        int q=0;
        for(int i=c.length-1;i>=0;i--)
            q+=c[i]*(int)Math.pow(2,c.length-i-1);
        return q;
    }

    public static void swap(int[] a,int[] b,int start){
        int tmp;
        for(int i=start;i<a.length;i++){
            tmp=a[i];
            a[i]=b[i];
            b[i]=tmp;
        }
    }
    public static void mutate(int[][] p){
        Random r = new Random();
        double random=0;
        for(int i=0;i<p.length;i++){
            for(int j=0;j<p[0].length;j++){
                random=r.nextDouble();
                if(random<pmut) {
                    if(p[i][j]==0) p[i][j]=1;
                    else p[i][j]=0;
                }
            }
        }
    }
    public static int[][] crossover_mutation_election(int[][] p){
        Random r = new Random();
        int[][] shuffledArray=new int[n][l];
        List<Integer> solution = new ArrayList<>();
        for (int i = 0; i <= 19; i++) {
            solution.add(i);
        }
        Collections.shuffle(solution);
        int[] indexes=new int[20];
        for (int i = 0; i <= 19; i++) {
            indexes[i]=solution.get(i);
        }
        for (int i = 0; i <= 19; i++)
            for (int j = 0; j <= 9; j++)
                shuffledArray[i][j]=p[indexes[i]][j];




        int[][] group1 = new int[n/2][l];
        int[][] group2 = new int[n/2][l];

        for(int i=0;i<n/2;i++){
            for (int j = 0; j <= 9; j++)
                group1[i][j]=shuffledArray[i][j];
        }

        for(int i=0;i<n/2;i++){
            for (int j = 0; j <= 9; j++)
                group2[i][j]=shuffledArray[i+n/2][j];
        }



        int[][] childs1=new int[n/2][l];
        int[][] childs2=new int[n/2][l];
        for(int i=0;i<group1.length;i++){
            for(int j=0;j<10;j++){
                childs1[i][j]=group1[i][j];
                childs2[i][j]=group2[i][j];
            }
        }

        double random;
        int start;
        for(int i=0;i<childs1.length;i++){
            random=r.nextDouble();
            if(random<pcross){
                start=r.nextInt(10);

                swap(childs1[i],childs2[i],start);
            }
        }

        mutate(childs1);
        mutate(childs2);

        int[][] result=new int[n][l];
        int v1=0,v2=0,v3=0,v4=0;
        int[] sorted=new int[4];
        int index=0;
        int increment=0;
        for(int i=0;i<group1.length;i++){
            increment=0;
            v1=getFitness(group1[i]);
            v2=getFitness(group2[i]);
            v3=getFitness(childs1[i]);
            v4=getFitness(childs2[i]);
            sorted[0]=v1;
            sorted[1]=v2;
            sorted[2]=v3;
            sorted[3]=v4;
            Arrays.sort(sorted);
            if(getFitness(group1[i])==sorted[3] ||getFitness(group1[i])==sorted[2] ){ result[index++]=group1[i]; increment++;}
            if(getFitness(group2[i])==sorted[3] ||getFitness(group2[i])==sorted[2] ){ result[index++]=group2[i]; increment++;}
            if((getFitness(childs1[i])==sorted[3] ||getFitness(childs1[i])==sorted[2])&&increment<2 ){ result[index++]=childs1[i];increment++;}
            if((getFitness(childs2[i])==sorted[3] ||getFitness(childs2[i])==sorted[2])&& increment<2){ result[index++]=childs2[i];}
        }


        return result;
    }
    static double variance(double a[],
                           int n)
    {

        double sum = 0;

        for (int i = 0; i < n; i++)
            sum += a[i];
        double mean = (double)sum /
                (double)n;


        double sqDiff = 0;
        for (int i = 0; i < n; i++)
            sqDiff += (a[i] - mean) *
                    (a[i] - mean);

        return (double)sqDiff / n;
    }
}