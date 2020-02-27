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

import java.util.Arrays;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.router.MainModeIdentifier;
import org.matsim.core.scenario.ScenarioUtils;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
* @author ikaddoura
*/

public final class RunBerlinScenario {

<<<<<<< HEAD
	private static final Logger log = Logger.getLogger(RunBerlinScenario.class);
	private static final String letter = "A";
	private static final String percent = "10";
=======
	private static final Logger log = Logger.getLogger(RunBerlinScenario.class );
>>>>>>> branch '5.4.x' of https://github.com/rkoetsch/matsim-berlin.git

	public static void main(String[] args) {
		
		for (String arg : args) {
			log.info( arg );
		}
<<<<<<< HEAD

		if (args.length == 0) {
			args = new String[] { "D:\\Rico\\" + percent + "pc\\100v8\\scenario-" + letter + "\\finalIteration\\final"
					+ letter + ".config.xml" };
//			args = new String[] { "C:\\Users\\koetscha\\Documents\\abstractSzenario\\finalA.config.xml" };
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

		long[] tour = new long[] { 100163057, 3712222554l, 26870674, 29218295, 29270520, 29785890, 282395034, 268224213,
				4313424156l, 275726428, 1380016717, 677228677, 26754202, 274977654, 29686277, 26785807, 269843861,
				26761185, 26554202, 254870237 };

		double[] expectedAvgTT = new double[] { 0, 1386.78514, 461.0143096, 660.7601614, 482.7117253, 531.2474148,
				615.2137168, 636.927544, 524.2512817, 773.1225155, 800.9296293, 404.1527514, 525.0083581, 599.2197634,
				593.5771799, 760.3694996, 389.5929394, 753.3953592, 605.2273493, 837.7488374 };

		double[] expectedMinTT = new double[] { 0, 1269.541339, 397.5076803, 625.1344664, 431.6207752, 490.3279327,
				592.9618135, 589.0757879, 469.5942699, 720.6765824, 725.6061951, 379.5941993, 495.9362131, 545.9396331,
				525.6032059, 662.4026367, 345.5011254, 656.0057098, 512.9801903, 743.2175726 };

		double[] expectedMaxTT = new double[] { 0, 1592.517231, 533.4664708, 738.1203464, 582.9350352, 582.2497275,
				663.5987905, 702.907558, 653.5758981, 896.8119032, 892.120581, 445.7765853, 560.4866599, 667.6098697,
				677.0537879, 839.0182749, 474.0636164, 887.3781677, 750.0190174, 1049.122674 };

		long[] tourXL = new long[] { 100163057, 3712222554l, 26870674, 29218295, 29270520, 29785890, 282395034,
				268224213, 4313424156l, 275726428, 1380016717, 677228677, 26754202, 274977654, 29686277, 26785807,
				269843861, 26761185, 26554202, 254870237, 100163057, 3712222554l, 26870674, 29218295, 29270520,
				29785890, 282395034, 268224213, 4313424156l, 275726428, 1380016717, 677228677, 26754202, 274977654,
				29686277, 26785807, 269843861, 26761185, 26554202, 254870237, 100163057, 3712222554l, 26870674,
				29218295, 29270520, 29785890, 282395034, 268224213, 4313424156l, 275726428, 1380016717, 677228677,
				26754202, 274977654, 29686277, 26785807, 269843861, 26761185, 26554202, 254870237, 100163057,
				3712222554l, 26870674, 29218295, 29270520, 29785890, 282395034, 268224213, 4313424156l, 275726428,
				1380016717, 677228677, 26754202, 274977654, 29686277, 26785807, 269843861, 26761185, 26554202,
				254870237, 100163057, 3712222554l, 26870674, 29218295, 29270520, 29785890, 282395034, 268224213,
				4313424156l, 275726428, 1380016717, 677228677, 26754202, 274977654, 29686277, 26785807, 269843861,
				26761185, 26554202, 254870237, 100163057, 3712222554l, 26870674, 29218295, 29270520, 29785890,
				282395034, 268224213, 4313424156l, 275726428, 1380016717, 677228677, 26754202, 274977654, 29686277,
				26785807, 269843861, 26761185, 26554202, 254870237, 100163057, 3712222554l, 26870674, 29218295,
				29270520, 29785890, 282395034, 268224213, 4313424156l, 275726428, 1380016717, 677228677, 26754202,
				274977654, 29686277, 26785807, 269843861, 26761185, 26554202, 254870237 };

		double[] expectedAvgTTXL = new double[] { 0, 1386.78514, 461.0143096, 660.7601614, 482.7117253, 531.2474148,
				615.2137168, 636.927544, 524.2512817, 773.1225155, 800.9296293, 404.1527514, 525.0083581, 599.2197634,
				593.5771799, 760.3694996, 389.5929394, 753.3953592, 605.2273493, 837.7488374, 784.4261075, 1386.78514,
				461.0143096, 660.7601614, 482.7117253, 531.2474148, 615.2137168, 636.927544, 524.2512817, 773.1225155,
				800.9296293, 404.1527514, 525.0083581, 599.2197634, 593.5771799, 760.3694996, 389.5929394, 753.3953592,
				605.2273493, 837.7488374, 784.4261075, 1386.78514, 461.0143096, 660.7601614, 482.7117253, 531.2474148,
				615.2137168, 636.927544, 524.2512817, 773.1225155, 800.9296293, 404.1527514, 525.0083581, 599.2197634,
				593.5771799, 760.3694996, 389.5929394, 753.3953592, 605.2273493, 837.7488374, 784.4261075, 1386.78514,
				461.0143096, 660.7601614, 482.7117253, 531.2474148, 615.2137168, 636.927544, 524.2512817, 773.1225155,
				800.9296293, 404.1527514, 525.0083581, 599.2197634, 593.5771799, 760.3694996, 389.5929394, 753.3953592,
				605.2273493, 837.7488374, 784.4261075, 1386.78514, 461.0143096, 660.7601614, 482.7117253, 531.2474148,
				615.2137168, 636.927544, 524.2512817, 773.1225155, 800.9296293, 404.1527514, 525.0083581, 599.2197634,
				593.5771799, 760.3694996, 389.5929394, 753.3953592, 605.2273493, 837.7488374, 784.4261075, 1386.78514,
				461.0143096, 660.7601614, 482.7117253, 531.2474148, 615.2137168, 636.927544, 524.2512817, 773.1225155,
				800.9296293, 404.1527514, 525.0083581, 599.2197634, 593.5771799, 760.3694996, 389.5929394, 753.3953592,
				605.2273493, 837.7488374, 784.4261075, 1386.78514, 461.0143096, 660.7601614, 482.7117253, 531.2474148,
				615.2137168, 636.927544, 524.2512817, 773.1225155, 800.9296293, 404.1527514, 525.0083581, 599.2197634,
				593.5771799, 760.3694996, 389.5929394, 753.3953592, 605.2273493, 837.7488374 };

		double[] expectedMinTTXL = new double[] { 0, 1269.541339, 397.5076803, 625.1344664, 431.6207752, 490.3279327,
				592.9618135, 589.0757879, 469.5942699, 720.6765824, 725.6061951, 379.5941993, 495.9362131, 545.9396331,
				525.6032059, 662.4026367, 345.5011254, 656.0057098, 512.9801903, 743.2175726, 687.2322049, 1269.541339,
				397.5076803, 625.1344664, 431.6207752, 490.3279327, 592.9618135, 589.0757879, 469.5942699, 720.6765824,
				725.6061951, 379.5941993, 495.9362131, 545.9396331, 525.6032059, 662.4026367, 345.5011254, 656.0057098,
				512.9801903, 743.2175726, 687.2322049, 1269.541339, 397.5076803, 625.1344664, 431.6207752, 490.3279327,
				592.9618135, 589.0757879, 469.5942699, 720.6765824, 725.6061951, 379.5941993, 495.9362131, 545.9396331,
				525.6032059, 662.4026367, 345.5011254, 656.0057098, 512.9801903, 743.2175726, 687.2322049, 1269.541339,
				397.5076803, 625.1344664, 431.6207752, 490.3279327, 592.9618135, 589.0757879, 469.5942699, 720.6765824,
				725.6061951, 379.5941993, 495.9362131, 545.9396331, 525.6032059, 662.4026367, 345.5011254, 656.0057098,
				512.9801903, 743.2175726, 687.2322049, 1269.541339, 397.5076803, 625.1344664, 431.6207752, 490.3279327,
				592.9618135, 589.0757879, 469.5942699, 720.6765824, 725.6061951, 379.5941993, 495.9362131, 545.9396331,
				525.6032059, 662.4026367, 345.5011254, 656.0057098, 512.9801903, 743.2175726, 687.2322049, 1269.541339,
				397.5076803, 625.1344664, 431.6207752, 490.3279327, 592.9618135, 589.0757879, 469.5942699, 720.6765824,
				725.6061951, 379.5941993, 495.9362131, 545.9396331, 525.6032059, 662.4026367, 345.5011254, 656.0057098,
				512.9801903, 743.2175726, 687.2322049, 1269.541339, 397.5076803, 625.1344664, 431.6207752, 490.3279327,
				592.9618135, 589.0757879, 469.5942699, 720.6765824, 725.6061951, 379.5941993, 495.9362131, 545.9396331,
				525.6032059, 662.4026367, 345.5011254, 656.0057098, 512.9801903, 743.2175726 };

		double[] expectedMaxTTXL = new double[] { 0, 1592.517231, 533.4664708, 738.1203464, 582.9350352, 582.2497275,
				663.5987905, 702.907558, 653.5758981, 896.8119032, 892.120581, 445.7765853, 560.4866599, 667.6098697,
				677.0537879, 839.0182749, 474.0636164, 887.3781677, 750.0190174, 1049.122674, 896.4662972, 1592.517231,
				533.4664708, 738.1203464, 582.9350352, 582.2497275, 663.5987905, 702.907558, 653.5758981, 896.8119032,
				892.120581, 445.7765853, 560.4866599, 667.6098697, 677.0537879, 839.0182749, 474.0636164, 887.3781677,
				750.0190174, 1049.122674, 896.4662972, 1592.517231, 533.4664708, 738.1203464, 582.9350352, 582.2497275,
				663.5987905, 702.907558, 653.5758981, 896.8119032, 892.120581, 445.7765853, 560.4866599, 667.6098697,
				677.0537879, 839.0182749, 474.0636164, 887.3781677, 750.0190174, 1049.122674, 896.4662972, 1592.517231,
				533.4664708, 738.1203464, 582.9350352, 582.2497275, 663.5987905, 702.907558, 653.5758981, 896.8119032,
				892.120581, 445.7765853, 560.4866599, 667.6098697, 677.0537879, 839.0182749, 474.0636164, 887.3781677,
				750.0190174, 1049.122674, 896.4662972, 1592.517231, 533.4664708, 738.1203464, 582.9350352, 582.2497275,
				663.5987905, 702.907558, 653.5758981, 896.8119032, 892.120581, 445.7765853, 560.4866599, 667.6098697,
				677.0537879, 839.0182749, 474.0636164, 887.3781677, 750.0190174, 1049.122674, 896.4662972, 1592.517231,
				533.4664708, 738.1203464, 582.9350352, 582.2497275, 663.5987905, 702.907558, 653.5758981, 896.8119032,
				892.120581, 445.7765853, 560.4866599, 667.6098697, 677.0537879, 839.0182749, 474.0636164, 887.3781677,
				750.0190174, 1049.122674, 896.4662972, 1592.517231, 533.4664708, 738.1203464, 582.9350352, 582.2497275,
				663.5987905, 702.907558, 653.5758981, 896.8119032, 892.120581, 445.7765853, 560.4866599, 667.6098697,
				677.0537879, 839.0182749, 474.0636164, 887.3781677, 750.0190174, 1049.122674 };

		double economy = 5 * 2 * 60;
		double premium = 1 * 2 * 60;

		runCalculations(network, calc, tour, true, expectedAvgTT, economy, "avgEconomy");
		runCalculations(network, calc, tour, true, expectedAvgTT, premium, "avgPremium");

		runCalculations(network, calc, tour, true, expectedMinTT, economy, "minEconomy");
		runCalculations(network, calc, tour, true, expectedMinTT, premium, "minPremium");

		runCalculations(network, calc, tour, true, expectedMaxTT, economy, "maxEconomy");
		runCalculations(network, calc, tour, true, expectedMaxTT, premium, "maxPremium");

		runCalculations(network, calc, tourXL, true, expectedAvgTTXL, economy, "avgEconomyXL");
		runCalculations(network, calc, tourXL, true, expectedAvgTTXL, premium, "avgPremiumXL");

		runCalculations(network, calc, tourXL, true, expectedMinTTXL, economy, "minEconomyXL");
		runCalculations(network, calc, tourXL, true, expectedMinTTXL, premium, "minPremiumXL");

		runCalculations(network, calc, tourXL, true, expectedMaxTTXL, economy, "maxEconomyXL");
		runCalculations(network, calc, tourXL, true, expectedMaxTTXL, premium, "maxPremiumXL");

//		printExampleRoutes(network, calc);
//
//		printMatrix("berlin", network, calc,
//				new long[] { 26554202, 26754202, 26761185, 26785807, 26870674, 29218295, 29270520, 29686277, 29785890,
//						100163057, 254870237, 268224213, 269843861, 274977654, 275726428, 282395034, 677228677,
//						1380016717, 3712222554l, 4313424156l });
//
//		printMatrix("partOfBerlin", network, calc,
//				new long[] { 26682577, 26736488, 26738756, 26787062, 26840975, 26849117, 26908562, 27212150, 27212418,
//						27501787, 27785308, 27786937, 28196764, 237203812, 447907184, 546901616, 846642205, 1054891928,
//						1949782772, 2353609856l });

	}

	private static void runCalculations(Network network, LeastCostPathCalculator calc, long[] tour,
			boolean planedDeparture, double[] expectedTT, double timewindow, String name) {

		try {
			FileWriter csvWriter = new FileWriter(name + ".csv");
			csvWriter.append(
					"hour; customer; from; to; oArrival; oServiceTime; oDeparture; odTravelTime; dArrival; dServiceTime; dDeparture; doTravelTime;;"
							+ "oExpectedArrival; oTwStart; oTwEnd; oBeforeTW; oInTW; oAfterTW; oEarlyTW; oLateTW; oEarly; oLate;;"
							+ "dExpectedArrival; dTwStart; dTwEnd; dBeforeTW; dInTW; dAfterTW; dEarlyTW; oLateTW; dEarly; dLate;;"
							+ "odPath; nextPath\n");

			// do it
			for (int i = 0; i < 24; i++)
				calculateTravelTimes(network, calc, tour, i, planedDeparture, expectedTT, timewindow, csvWriter);

			// finish
			csvWriter.flush();
			csvWriter.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void calculateTravelTimes(Network network, LeastCostPathCalculator calc, long[] tour, int hour,
			boolean planedArrival, double[] expectedTT, double timewindow, FileWriter csvWriter) throws IOException {

		double start = hour * 3600;
		double serviceTime = 2 * 60;

		double[] expectedArrival = new double[expectedTT.length];
		if (planedArrival) {
			expectedArrival[0] = start + expectedTT[0];
			for (int x = 1; x < expectedArrival.length; x++)
				expectedArrival[x] = expectedArrival[x - 1] + serviceTime + expectedTT[x];
		}

		for (int customer = 0; customer < tour.length / 2; customer++) {
			int x = customer * 2;

			// ArrayIndexOutOfBoundsException: 134
			double oTwStart = expectedArrival[x] - timewindow / 2;
			double oTwEnd = expectedArrival[x] + timewindow / 2;
			double oArrival = start;

			boolean oBeforeTW = false;
			boolean oInTW = false;
			boolean oAfterTW = false;
			double oEarlyTW = 0;
			double oLateTW = 0;
			double oEarly = 0;
			double oLate = 0;

			// before arrival tw
			if (oArrival < oTwStart) {
				oEarlyTW = oTwStart - oArrival;
				oBeforeTW = true;
			}
			// in arrival tw
			if (oArrival >= oTwStart && oArrival <= oTwEnd)
				oInTW = true;
			// after arrival tw
			if (oArrival > oTwEnd) {
				oLateTW = oArrival - oTwEnd;
				oAfterTW = true;
			}
			// before expected arrival
			if (oArrival < expectedArrival[x])
				oEarly = expectedArrival[x] - oArrival;
			// after expected arrival
			if (oArrival > expectedArrival[x])
				oLate = oArrival - expectedArrival[x];

			if (planedArrival && oArrival < oTwStart) {
				oArrival = oTwStart;
			}

			double oServiceTime = serviceTime;
			double oDeparture = oArrival + oServiceTime;

			long originID = tour[x];
			long destID = tour[x + 1];
			Node origin = network.getNodes().get(Id.createNodeId(originID));
			Node destination = network.getNodes().get(Id.createNodeId(destID));

			Path odPath = getTravelPath(calc, origin, destination, oDeparture);
			double odTravelTime = odPath.travelTime;

			double dArrival = oDeparture + odTravelTime;
			double dServiceTime = serviceTime;
			double dDeparture = dArrival + dServiceTime;

//			ArrayIndexOutOfBoundsException: 19
			double dTwStart = expectedArrival[x + 1] - timewindow / 2;
			double dTwEnd = expectedArrival[x + 1] + timewindow / 2;
			boolean dBeforeTW = false;
			boolean dInTW = false;
			boolean dAfterTW = false;
			double dEarlyTW = 0;
			double dLateTW = 0;
			double dEarly = 0;
			double dLate = 0;

			// before arrival tw
			if (dArrival < dTwStart) {
				dEarlyTW = dTwStart - dArrival;
				dBeforeTW = true;
			}
			// in arrival tw
			if (dArrival >= dTwStart && dArrival <= dTwEnd)
				dInTW = true;
			// after arrival tw
			if (dArrival > dTwEnd) {
				dLateTW = dArrival - dTwEnd;
				dAfterTW = true;
			}
			// before expected arrival
			if (dArrival < expectedArrival[x + 1])
				dEarly = expectedArrival[x + 1] - dArrival;
			// after expected arrival
			if (dArrival > expectedArrival[x + 1])
				dLate = dArrival - expectedArrival[x + 1];

			double nextTravelTime = 0;
			Path nextPath = null;
			if (x < tour.length - 2) {
				long idNext = tour[x + 2];
				Node nextNode = network.getNodes().get(Id.createNodeId(idNext));
				nextPath = getTravelPath(calc, destination, nextNode, dDeparture);
				nextTravelTime = nextPath.travelTime;
			}
			start = dDeparture + nextTravelTime;

			String str = hour + ";" + (customer + 1) + ";" + originID + ";" + destID + ";" + oArrival + ";"
					+ oServiceTime + ";" + oDeparture + ";" + odTravelTime + ";" + dArrival + ";" + dServiceTime + ";"
					+ dDeparture + ";" + nextTravelTime + ";;" + expectedArrival[x] + ";" + oTwStart + ";" + oTwEnd
					+ ";" + oBeforeTW + ";" + oInTW + ";" + oAfterTW + ";" + oEarlyTW + ";" + oLateTW + ";" + oEarly
					+ ";" + oLate + ";;" + expectedArrival[x + 1] + ";" + dTwStart + ";" + dTwEnd + ";" + dBeforeTW
					+ ";" + dInTW + ";" + dAfterTW + ";" + dEarlyTW + ";" + dLateTW + ";" + dEarly + ";" + dLate + ";;";

			for (Node node : odPath.nodes) {
				str = str + node.getId() + ",";
			}

			if (nextPath != null) {
				str = str.substring(0, str.length() - 1) + "; ";
				for (Node node : nextPath.nodes) {
					str = str + node.getId() + ",";
				}
			}
			str = str.substring(0, str.length() - 1) + "\n";
			csvWriter.append(str);
		}
	}

	private static Path getTravelPath(LeastCostPathCalculator calc, Node from, Node to, double departure) {
		double day = 24 * 60 * 60;
		departure = departure % day;
		return calc.calcLeastCostPath(from, to, departure, null, null);
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

=======
		
		if ( args.length==0 ) {
			args = new String[] {"scenarios/berlin-v5.4-10pct/input/berlin-v5.4-10pct.config.xml"}  ;
		}

		Config config = prepareConfig( args ) ;
		Scenario scenario = prepareScenario( config ) ;
		Controler controler = prepareControler( scenario ) ;
		controler.run() ;

	}

	public static Controler prepareControler( Scenario scenario ) {
		// note that for something like signals, and presumably drt, one needs the controler object
		
>>>>>>> branch '5.4.x' of https://github.com/rkoetsch/matsim-berlin.git
		Gbl.assertNotNull(scenario);
		
		final Controler controler = new Controler( scenario );
		
		if (controler.getConfig().transit().isUsingTransitInMobsim()) {
			// use the sbb pt raptor router
			controler.addOverridingModule( new AbstractModule() {
				@Override
				public void install() {
					install( new SwissRailRaptorModule() );
				}
			} );
		} else {
			log.warn("Public transit will be teleported and not simulated in the mobsim! "
					+ "This will have a significant effect on pt-related parameters (travel times, modal split, and so on). "
					+ "Should only be used for testing or car-focused studies with a fixed modal split.  ");
		}
		
		// use the (congested) car travel time for the teleported ride mode
		controler.addOverridingModule( new AbstractModule() {
			@Override
			public void install() {
				addTravelTimeBinding( TransportMode.ride ).to( networkTravelTime() );
				addTravelDisutilityFactoryBinding( TransportMode.ride ).to( carTravelDisutilityFactoryKey() );
			}
		} );

		return controler;
	}
	
	public static Scenario prepareScenario( Config config ) {
		Gbl.assertNotNull( config );
		
		// note that the path for this is different when run from GUI (path of original config) vs.
		// when run from command line/IDE (java root).  :-(    See comment in method.  kai, jul'18
		// yy Does this comment still apply?  kai, jul'19

		final Scenario scenario = ScenarioUtils.loadScenario( config );

		return scenario;
	}
	
	public static Config prepareConfig( String [] args ) {
		OutputDirectoryLogging.catchLogEntries();
		
		String[] typedArgs = Arrays.copyOfRange( args, 1, args.length );

		final Config config = ConfigUtils.loadConfig( args[ 0 ] ); // I need this to set the context
		
		config.controler().setRoutingAlgorithmType( FastAStarLandmarks );
		
		config.subtourModeChoice().setProbaForRandomSingleTripMode( 0.5 );
		
		config.plansCalcRoute().setRoutingRandomness( 3. );
		config.plansCalcRoute().removeModeRoutingParams(TransportMode.ride);
		config.plansCalcRoute().removeModeRoutingParams(TransportMode.pt);
		config.plansCalcRoute().removeModeRoutingParams(TransportMode.bike);
		config.plansCalcRoute().removeModeRoutingParams("undefined");
	
		config.qsim().setInsertingWaitingVehiclesBeforeDrivingVehicles( true );
				
		// vsp defaults
		config.vspExperimental().setVspDefaultsCheckingLevel( VspExperimentalConfigGroup.VspDefaultsCheckingLevel.info );
		config.plansCalcRoute().setInsertingAccessEgressWalk( true );
		config.qsim().setUsingTravelTimeCheckInTeleportation( true );
		config.qsim().setTrafficDynamics( TrafficDynamics.kinematicWaves );
				
		// activities:
		for ( long ii = 600 ; ii <= 97200; ii+=600 ) {
			config.planCalcScore().addActivityParams( new ActivityParams( "home_" + ii + ".0" ).setTypicalDuration( ii ) );
			config.planCalcScore().addActivityParams( new ActivityParams( "work_" + ii + ".0" ).setTypicalDuration( ii ).setOpeningTime(6. * 3600. ).setClosingTime(20. * 3600. ) );
			config.planCalcScore().addActivityParams( new ActivityParams( "leisure_" + ii + ".0" ).setTypicalDuration( ii ).setOpeningTime(9. * 3600. ).setClosingTime(27. * 3600. ) );
			config.planCalcScore().addActivityParams( new ActivityParams( "shopping_" + ii + ".0" ).setTypicalDuration( ii ).setOpeningTime(8. * 3600. ).setClosingTime(20. * 3600. ) );
			config.planCalcScore().addActivityParams( new ActivityParams( "other_" + ii + ".0" ).setTypicalDuration( ii ) );
		}
		config.planCalcScore().addActivityParams( new ActivityParams( "freight" ).setTypicalDuration( 12.*3600. ) );

		ConfigUtils.applyCommandline( config, typedArgs ) ;

		return config ;
	}

}

