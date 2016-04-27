package benchmark;

import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.sat4j.specs.TimeoutException;

import LTLparser.LTLNode;
import LTLparser.LTLParserProvider;
import automata.safa.SAFA;
import automata.safa.booleanexpression.SumOfProductsFactory;
import benchmark.ltlconverter.LTLConverter;
import logic.ltl.LTLFormula;
import theory.bdd.BDD;
import theory.bddalgebra.BDDSolver;
import utilities.Pair;

public class RunExperiments {
	static long timeout = 2000;
	static boolean normalize = true;

	static String emptinessOutputFile = "results/emptiness.csv";
	static String equivalenceOutputFile = "results/selfEquivalence.csv";
	static String suffix = "";

	public static void main(String[] args) throws InterruptedException {

		RunLTLEmptiness();
		RunLTLSelfEquivalence();

	}

	public static void RunLTLEmptiness() {
		try {
			FileWriter fw = new FileWriter(emptinessOutputFile);
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {// &&
													// filePath.toString().endsWith("lift_7.ltl"))
													// {
					try {
						List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));

						System.out.println(filePath);

						int counter = 0;
						for (LTLNode ltl : nodes) {
							fw.append(filePath.getFileName().toString());
							if (counter > 0)
								fw.append(counter + "");
							fw.append(", ");
							counter++;

							Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
							BDDSolver bdds = pair.first;
							LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
							SAFA<BDD, BDD> safa = tot.getSAFA(bdds, normalize);

							fw.append(pair.second.getSize() + ", ");
							
							boolean result = true;
							boolean to1 = false;
							boolean to2 = false;

							long startTime1 = System.currentTimeMillis();
							try {
								result = SAFA.isEmpty(safa, bdds, timeout);
								fw.append(System.currentTimeMillis() - startTime1 + ", ");
							} catch (TimeoutException toe) {
								fw.append("TO,");
								to1 = true;
							}

							long startTime2 = System.currentTimeMillis();
							try {
								result = SAFA.areReverseEquivalent(safa, SAFA.getEmptySAFA(bdds), bdds, timeout).first;
								fw.append(System.currentTimeMillis() - startTime2 + ", ");
							} catch (TimeoutException toe) {
								fw.append("TO,");
								to2 = true;
							}

							if (!(to1 && to2))
								fw.append(result + "");
							else
								fw.append("TO");

							fw.append("\n");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void RunLTLSelfEquivalence() {
		try {
			FileWriter fw = new FileWriter(equivalenceOutputFile);
			fw.append("formula, size, congruence, reverse, result \n");
			Files.walk(Paths.get("../automatark/LTL/")).forEach(filePath -> {
				if (Files.isRegularFile(filePath) && filePath.toString().endsWith(suffix)
						&& filePath.toString().endsWith(suffix)) {
					try {
						List<LTLNode> nodes = LTLParserProvider.parse(new FileReader(filePath.toFile()));
						System.out.println(filePath);
						int counter = 0;
						for (LTLNode ltl : nodes) {
							fw.append(filePath.getFileName().toString());
							if (counter > 0)
								fw.append(counter + "");
							fw.append(", ");
							counter++;

							Pair<BDDSolver, LTLFormula<BDD, BDD>> pair = LTLConverter.getLTLBDD(ltl);
							BDDSolver bdds = pair.first;
							LTLFormula<BDD, BDD> tot = pair.second.pushNegations(bdds);
							SAFA<BDD, BDD> safa = tot.getSAFA(bdds, normalize);

							fw.append(pair.second.getSize() + ", ");

							boolean result = true;
							boolean to1 = false;
							boolean to2 = false;

							long startTime1 = System.currentTimeMillis();
							try {
								result = SAFA.isEquivalent(safa, safa, bdds, SumOfProductsFactory.getInstance(), timeout)
										.getFirst();
								fw.append(System.currentTimeMillis() - startTime1 + ",");
								if (!result)
									System.out.println("Error in equiv algo, self equiv returns false");
							} catch (TimeoutException toe) {
								fw.append("TO,");
								to1 = true;
							}

							long startTime2 = System.currentTimeMillis();
							try {
								result = SAFA.areReverseEquivalent(safa, safa, bdds, timeout).first;
								fw.append(System.currentTimeMillis() - startTime2 + ",");
								if (!result)
									System.out.println("Error in equiv algo, self equiv returns false");
							} catch (TimeoutException toe) {
								fw.append("TO,");
								to2 = true;
							}

							if (!(to1 && to2))
								fw.append(result + "");
							else
								fw.append("TO");

							fw.append("\n");
						}
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			});
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}