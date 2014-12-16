package main;

import java.util.ArrayList;
import java.util.List;

import entities.Entity;
import readData.ReadClassifiedFile;
import readData.ReadFileToClassify;
import readData.ReadFileTraining;
import statistics.EmissionProbability;
import statistics.StartProbability;
import statistics.Statistics;
import statistics.TransmissionProbability;
import viterbi.Viterbi;
import viterbi.Viterbi.ObservationException;

// TODO: Auto-generated Javadoc
/**
 * The Class Main.
 */
public class Main {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		String trainingFile = "testDocs/trainingArticles.txt";
		String testFile = "testDocs/testArticle.txt";
		String classifiedFile = "testDOcs/textClassified.txt";
		ReadFileTraining read = new ReadFileTraining(trainingFile);
		System.out.println("Start");
		StartProbability startProb = new StartProbability(read.getWordList(), read.getEntityList());
		System.out.println("Emission");
		EmissionProbability emi = new EmissionProbability(read.getEntityList(), read.getWordList());
		System.out.println("Transmission");
		TransmissionProbability trans = new TransmissionProbability(trainingFile);
		trans.runTransmissionProbabilistic();
		System.out.println("Classify this");
		ReadFileToClassify classificationFile = new ReadFileToClassify(testFile);
		System.out.println("Classify");
		Viterbi viterbi = new Viterbi(null, trans.getTable(), emi.getTable(), startProb.getStartProbability());
		ArrayList<ArrayList<String>> linesToClassify = classificationFile.getWordLines();
		ArrayList<Entity> entities = new ArrayList<Entity>();
		List<Entity> classifiedStates;
		for (ArrayList<String> line : linesToClassify) {
			if (line == null) continue;
			viterbi.setObservations(line);
			try {
				viterbi.run();
				classifiedStates = viterbi.getStates();
				for(int i = 0; i < line.size(); i++) {
					System.out.println("Word: " + line.get(i) + " , Entity: " + classifiedStates.get(i));
					entities.add(classifiedStates.get(i));
				}
			} catch (ObservationException e) {
				e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		

		ReadClassifiedFile classified = new ReadClassifiedFile(classifiedFile);
		Statistics statistics = new Statistics(entities,classified.getEntities());
//		System.out.println("Correct " + statistics.getCorrect());
//		System.out.println("Error " + statistics.getError());
//		System.out.println("Total " + statistics.getTotal());
		System.out.println("Statistics for classification of file: "+ testFile);
		System.out.println("CRate" + statistics.getCorrectRate());
		System.out.println("ERate" + statistics.getErrorRate());
		
	}
}
