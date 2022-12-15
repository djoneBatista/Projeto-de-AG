package hibrido;

public class Genetic {

    public double globalError = 1;
    public static final int POPULATION_SIZE = 7;
    public static final double MUTATION_PERCENT = 0.10;
    protected int matingPopulationSize = POPULATION_SIZE / 2;
    protected int favoredPopulationSize = matingPopulationSize / 2;
    protected double input[][];
    protected double ideal[][];
    protected Chromosome[] chromosomes;
    public Rede minhaRede;// esta e a rede para treinar  com AG, por isso evolucao dos pesos

    public double getGlobalError() {
        return globalError;
    }

    public Genetic(Rede red) {
        System.out.println("Genetic() constructor");
        this.minhaRede = red;
    }

    public void start() 
    {
        System.out.println("Geneticstar()");
        chromosomes = new Chromosome[POPULATION_SIZE];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            minhaRede.net.initialize();
            //reset()  inicializar os pesos da rede
            chromosomes[i] = new Chromosome(minhaRede);
            chromosomes[i].calculateCost(minhaRede, input);
            Chromosome c = chromosomes[i];
            c.fromMatrix();
            c.toMatrix();
        }
        Chromosome.sortChromosomes(chromosomes, chromosomes.length);
    }

    public void setInput(double input[][]) {
        this.input = input;
    }

    public void setIdeal(double ideal[][]) {
        this.ideal = ideal;
    }

    public void generation() {

        int ioffset = matingPopulationSize;
        int mutated = 0;
        double thisCost = 500.0;
//        double oldCost = 0.0;
//        double dcost = 500.0;
//        int countSame = 0;

        // Mate the chromosomes in the favoured population
        // with all in the mating population
        for (int i = 0; i < favoredPopulationSize - 1; i++) {
            Chromosome cmother = chromosomes[i];
            // Select partner from the mating population
            int father = (int) (0.999999 * Math.random() * (double) matingPopulationSize);
            Chromosome cfather = chromosomes[father];

            mutated += cmother.mate(cfather, chromosomes[ioffset], chromosomes[ioffset + 1]);
            ioffset += 2;
        }

        // The new generation is in the matingPopulation area
        // move them to the correct area for sort.
        for (int i = 0; i < matingPopulationSize; i++) {
            chromosomes[i] = chromosomes[i + matingPopulationSize];
            chromosomes[i].calculateCost(minhaRede, input);
        }

        // Now sort the new mating population
        Chromosome.sortChromosomes(chromosomes, matingPopulationSize);

        double cost = chromosomes[0].getCost();
//        dcost = Math.abs(cost - thisCost);
        thisCost = cost;
        //double mutationRate = 100.0 * (double) mutated / (double) matingPopulationSize;
        globalError = thisCost;
        chromosomes[0].toMatrix();
        minhaRede.fromArray(chromosomes[0].getMatrix());

    }

}
