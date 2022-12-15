package hibrido;
final class Chromosome {
    
    
    protected static final int GENE_SIZE = 64;
    protected int [] gene;
    protected double cost;
    protected double mutationPercent;
    protected int cutLength;
    double matrix[];
    
    
    Chromosome(Rede red) {
        matrix = red.toArray();
        gene = new int[matrix.length*GENE_SIZE];
        cost = 0.0;
        fromMatrix();
        cutLength = gene.length/2;
    }    
    public void toMatrix() {
        int idx = 0;
        
        for (int i=0;i<matrix.length;i++) {
            long l = 0;
            long or = 1;
            for (int j=0;j<GENE_SIZE;j++) {
                if ( gene[idx++]!=0 )
                    l = l | or;
                or+=or;
            }
            matrix[i] = Double.longBitsToDouble(l);
        }
        
    }    
    public void fromMatrix() {
        int idx = 0;
        for (int i=0;i<matrix.length;i++) {
            long l = Double.doubleToLongBits(matrix[i]);
            long and = 1;
            for (int j=0;j<GENE_SIZE;j++) {
                gene[idx++] = (l & and)!=0?1:0;
                and+=and;
            }
        }
    }    
    public void calculateCost(Rede red,double trial[][]) {
        toMatrix();
        red.fromArray(matrix);
        for (int i=0;i<trial.length;i++) {
            red.computeOutputs(); //vai salvando a melhor rede conforme o erro diminue
        }
        cost = red.getError(trial.length);
    }    
    double getCost() {
        return cost;
    }    
    int getGene(int i) {
        return gene[i];
    }    
    void setGenes(int [] list) {
        for ( int i=0;i<gene.length;i++ ) {
            gene[i] = list[i];
        }
    }    
    void setCut(int cut) {
        cutLength = cut;
    }    
    void setMutation(double prob) {
        mutationPercent = prob;
    } 
    int mate(Chromosome father, Chromosome offspring1, Chromosome offspring2) {
        //int cutpoint = (int) (0.999999*Math.random()*(double)(gene.length-cutLength));
        
        int off1 [] = new int[gene.length];
        int off2 [] = new int[gene.length];
// mate
        for(int i=0;i<gene.length;i++) {
            if(Math.random()>0.5) {
                off1[i] = this.getGene(i);
                off2[i] = father.getGene(i);
            } else {
                off2[i] = this.getGene(i);
                off1[i] = father.getGene(i);
            }
        }
             
        int mutate = 0;
        if ( Math.random() < mutationPercent ) {
            int iswap1 = (int) (0.999999*Math.random()*(double)(gene.length));
            int iswap2 = (int) (0.999999*Math.random()*(double)gene.length);
            int i = off1[iswap1];
            off1[iswap1] = off1[iswap2];
            off1[iswap2] = i;
            mutate++;
        }
        if ( Math.random() < mutationPercent ) {
            int iswap1 = (int) (0.999999*Math.random()*(double)(gene.length));
            int iswap2 = (int) (0.999999*Math.random()*(double)gene.length);
            int i = off2[iswap1];
            off2[iswap1] = off2[iswap2];
            off2[iswap2] = i;
            mutate++;
        }
        
        // copy results
        offspring1.setGenes(off1);
        offspring1.toMatrix();
        offspring2.setGenes(off2);
        offspring2.toMatrix();
        
        return mutate;
    }    
    public static void sortChromosomes(Chromosome chromosomes[],int num) {
        Chromosome ctemp;
        boolean swapped = true;
        while ( swapped ) {
            swapped = false;
            for ( int i=0;i<num-1;i++ ) {
                if ( chromosomes[i].getCost() > chromosomes[i+1].getCost() ) {
                    ctemp = chromosomes[i];
                    chromosomes[i] = chromosomes[i+1];
                    chromosomes[i+1] = ctemp;
                    swapped = true;
                }
            }
        }
    }        
    public double[] getMatrix() {
        return matrix;
    }
    
   
}


