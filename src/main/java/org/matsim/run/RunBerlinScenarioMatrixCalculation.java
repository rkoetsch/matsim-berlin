/* *********************************************************************** *
 * project: org.matsim.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2017 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : info at matsim dot org                                *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *                                                                         *
 * *********************************************************************** */

package org.matsim.run;

import static org.matsim.core.config.groups.ControlerConfigGroup.RoutingAlgorithmType.FastAStarLandmarks;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.Node;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculator.Path;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
 * @author ikaddoura
 */
public final class RunBerlinScenarioMatrixCalculation {

	private static final Logger log = Logger.getLogger(RunBerlinScenarioMatrixCalculation.class);
	private static final String letter = "I";
	private static final String percent = "10";

	public static void main(String[] args) {

		for (String arg : args) {
			log.info(arg);
		}

		if (args.length == 0) {
			args = new String[] { "D:\\Rico\\" + percent + "pc\\100v8\\scenario-" + letter + "\\finalIteration\\final"
					+ letter + ".config.xml" };
		}

		Config config = prepareConfig(args);

		Scenario scenario = prepareScenario(config);
		Controler controler = prepareControler(scenario);

		config.controler().setLastIteration(0);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		controler.run();

		testMyStuff(controler);

	}

	private static void testMyStuff(Controler controler) {
		controler.getTripRouterProvider();
		LeastCostPathCalculatorFactory calcFac = controler.getLeastCostPathCalculatorFactory();
		TravelDisutilityFactory disuFac = controler.getTravelDisutilityFactory();

		Network network = controler.getScenario().getNetwork();
		TravelTime travelTimes = controler.getLinkTravelTimes();
		TravelTime timeCalculator = controler.getLinkTravelTimes();
		TravelDisutility travelCosts = disuFac.createTravelDisutility(timeCalculator);
		LeastCostPathCalculator calc = calcFac.createPathCalculator(network, travelCosts, travelTimes);

		printExampleRoutes(network, calc);
		
		printMatrix("berlin", network, calc,
				new long[] { 26554202, 26754202, 26761185, 26785807, 26870674, 29218295, 29270520, 29686277, 29785890,
						100163057, 254870237, 268224213, 269843861, 274977654, 275726428, 282395034, 677228677,
						1380016717, 3712222554l, 4313424156l });
		
		printMatrix("partOfBerlin", network, calc,
				new long[] { 26682577, 26736488, 26738756, 26787062, 26840975, 26849117, 26908562, 27212150, 27212418,
						27501787, 27785308, 27786937, 28196764, 237203812, 447907184, 546901616, 846642205, 1054891928,
						1949782772, 2353609856l });

	}

	private static void printMatrix(String name, Network network, LeastCostPathCalculator calc, long[] nodes) {
		Node fromNode;
		Node toNode;
		try {

			FileWriter csvWriter = new FileWriter(name + letter + percent + ".csv");
			csvWriter.append("from; to; hour; time; distance; path\n");
			// do it
			for (int x = 0; x < nodes.length; x++) {
				for (int y = 0; y < nodes.length; y++) {
					long idFrom = nodes[x];
					long idTo = nodes[y];
					fromNode = network.getNodes().get(Id.createNodeId(idFrom));
					toNode = network.getNodes().get(Id.createNodeId(idTo));
					print(calc, fromNode, toNode, csvWriter);
				}
			}

			// finish
			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void printExampleRoutes(Network network, LeastCostPathCalculator calc) {
		Node fromNode;
		Node toNode;
		try {

			FileWriter csvWriter = new FileWriter("examplePaths" + letter + percent + ".csv");
			csvWriter.append("from; to; hour; time; distance; path\n");

			// von TU to CBA
			fromNode = network.getNodes().get(Id.createNodeId(21590985));
			toNode = network.getNodes().get(Id.createNodeId(1822477776));
			print(calc, fromNode, toNode, csvWriter);

			// von MD to TU
			fromNode = network.getNodes().get(Id.createNodeId(266630013));
			toNode = network.getNodes().get(Id.createNodeId(21590985));
			print(calc, fromNode, toNode, csvWriter);
			// von NK to TU
			fromNode = network.getNodes().get(Id.createNodeId(1679931156));
			toNode = network.getNodes().get(Id.createNodeId(21590985));
			print(calc, fromNode, toNode, csvWriter);
			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void print(LeastCostPathCalculator calc, Node fromNode, Node toNode, FileWriter csvWriter)
			throws IOException {
		printPerHour(calc, fromNode, toNode, 0, csvWriter);
		printPerHour(calc, fromNode, toNode, 1, csvWriter);
		printPerHour(calc, fromNode, toNode, 2, csvWriter);
		printPerHour(calc, fromNode, toNode, 3, csvWriter);
		printPerHour(calc, fromNode, toNode, 4, csvWriter);
		printPerHour(calc, fromNode, toNode, 5, csvWriter);
		printPerHour(calc, fromNode, toNode, 6, csvWriter);
		printPerHour(calc, fromNode, toNode, 7, csvWriter);
		printPerHour(calc, fromNode, toNode, 8, csvWriter);
		printPerHour(calc, fromNode, toNode, 9, csvWriter);
		printPerHour(calc, fromNode, toNode, 10, csvWriter);
		printPerHour(calc, fromNode, toNode, 11, csvWriter);
		printPerHour(calc, fromNode, toNode, 12, csvWriter);
		printPerHour(calc, fromNode, toNode, 13, csvWriter);
		printPerHour(calc, fromNode, toNode, 14, csvWriter);
		printPerHour(calc, fromNode, toNode, 15, csvWriter);
		printPerHour(calc, fromNode, toNode, 16, csvWriter);
		printPerHour(calc, fromNode, toNode, 17, csvWriter);
		printPerHour(calc, fromNode, toNode, 18, csvWriter);
		printPerHour(calc, fromNode, toNode, 19, csvWriter);
		printPerHour(calc, fromNode, toNode, 20, csvWriter);
		printPerHour(calc, fromNode, toNode, 21, csvWriter);
		printPerHour(calc, fromNode, toNode, 22, csvWriter);
		printPerHour(calc, fromNode, toNode, 23, csvWriter);
	}

	private static void printPerHour(LeastCostPathCalculator calc, Node fromNode, Node toNode, int hour,
			FileWriter csvWriter) throws IOException {
		double starttime = hour * 3600;
		Path path = calc.calcLeastCostPath(fromNode, toNode, starttime, null, null);
		String str = fromNode.getId() + "; " + toNode.getId() + "; " + hour + "; " + path.travelTime + "; "
				+ path.travelCost + ";  ";
		for (Node node : path.nodes) {
			str = str + node.getId() + ",";
		}
		str = str.substring(0, str.length() - 1) + "\n";
		csvWriter.append(str);
	}

	public static Controler prepareControler(Scenario scenario) {
		// note that for something like signals, and presumably drt, one needs the
		// controler object

		Gbl.assertNotNull(scenario);

		final Controler controler = new Controler(scenario);

		if (controler.getConfig().transit().isUsingTransitInMobsim()) {
			// use the sbb pt raptor router
			controler.addOverridingModule(new AbstractModule() {
				@Override
				public void install() {
					install(new SwissRailRaptorModule());
				}
			});
		} else {
			log.warn("Public transit will be teleported and not simulated in the mobsim! "
					+ "This will have a significant effect on pt-related parameters (travel times, modal split, and so on). "
					+ "Should only be used for testing or car-focused studies with a fixed modal split.  ");
		}

		// use the (congested) car travel time for the teleported ride mode
		controler.addOverridingModule(new AbstractModule() {

			@Override
			public void install() {
				addTravelTimeBinding(TransportMode.ride).to(networkTravelTime());
				addTravelDisutilityFactoryBinding(TransportMode.ride).to(carTravelDisutilityFactoryKey());
			}

		});

		return controler;
	}

	public static Scenario prepareScenario(Config config) {
		Gbl.assertNotNull(config);

		// note that the path for this is different when run from GUI (path of original
		// config) vs.
		// when run from command line/IDE (java root). :-( See comment in method. kai,
		// jul'18
		// yy Does this comment still apply? kai, jul'19

		final Scenario scenario = ScenarioUtils.loadScenario(config);

		return scenario;
	}

	public static Config prepareConfig(String[] args) {
		OutputDirectoryLogging.catchLogEntries();

		String[] typedArgs = Arrays.copyOfRange(args, 1, args.length);

		final Config config = ConfigUtils.loadConfig(args[0]); // I need this to set the context

		config.controler().setRoutingAlgorithmType(FastAStarLandmarks);

		config.subtourModeChoice().setProbaForRandomSingleTripMode(0.5);

		config.plansCalcRoute().setRoutingRandomness(3.);
		config.plansCalcRoute().removeModeRoutingParams(TransportMode.ride);
		config.plansCalcRoute().removeModeRoutingParams(TransportMode.pt);
		config.plansCalcRoute().removeModeRoutingParams(TransportMode.bike);
		config.plansCalcRoute().removeModeRoutingParams("undefined");

		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles(true);

		// vsp defaults
		config.vspExperimental().setVspDefaultsCheckingLevel(VspExperimentalConfigGroup.VspDefaultsCheckingLevel.info);
		config.plansCalcRoute().setInsertingAccessEgressWalk(true);
		config.qsim().setUsingTravelTimeCheckInTeleportation(true);
		config.qsim().setTrafficDynamics(TrafficDynamics.kinematicWaves);

		// activities:
		for (long ii = 600; ii <= 97200; ii += 600) {
			config.planCalcScore().addActivityParams(new ActivityParams("home_" + ii + ".0").setTypicalDuration(ii));
			config.planCalcScore().addActivityParams(new ActivityParams("work_" + ii + ".0").setTypicalDuration(ii)
					.setOpeningTime(6. * 3600.).setClosingTime(20. * 3600.));
			config.planCalcScore().addActivityParams(new ActivityParams("leisure_" + ii + ".0").setTypicalDuration(ii)
					.setOpeningTime(9. * 3600.).setClosingTime(27. * 3600.));
			config.planCalcScore().addActivityParams(new ActivityParams("shopping_" + ii + ".0").setTypicalDuration(ii)
					.setOpeningTime(8. * 3600.).setClosingTime(20. * 3600.));
			config.planCalcScore().addActivityParams(new ActivityParams("other_" + ii + ".0").setTypicalDuration(ii));
		}
		config.planCalcScore().addActivityParams(new ActivityParams("freight").setTypicalDuration(12. * 3600.));

		ConfigUtils.applyCommandline(config, typedArgs);

		return config;
	}

}
