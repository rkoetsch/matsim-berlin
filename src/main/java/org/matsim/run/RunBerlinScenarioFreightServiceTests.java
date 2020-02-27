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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.contrib.freight.carrier.Carrier;
import org.matsim.contrib.freight.carrier.CarrierCapabilities;
import org.matsim.contrib.freight.carrier.CarrierCapabilities.FleetSize;
import org.matsim.contrib.freight.carrier.CarrierPlan;
import org.matsim.contrib.freight.carrier.CarrierService;
import org.matsim.contrib.freight.carrier.CarrierUtils;
import org.matsim.contrib.freight.carrier.CarrierVehicle;
import org.matsim.contrib.freight.carrier.CarrierVehicleTypes;
import org.matsim.contrib.freight.carrier.Carriers;
import org.matsim.contrib.freight.carrier.ScheduledTour;
import org.matsim.contrib.freight.carrier.TimeWindow;
import org.matsim.contrib.freight.carrier.Tour;
import org.matsim.contrib.freight.carrier.Tour.Leg;
import org.matsim.contrib.freight.controler.CarrierModule;
import org.matsim.contrib.freight.controler.CarrierPlanStrategyManagerFactory;
import org.matsim.contrib.freight.controler.CarrierScoringFunctionFactory;
import org.matsim.contrib.freight.jsprit.NetworkBasedTransportCosts;
import org.matsim.contrib.freight.jsprit.NetworkBasedTransportCosts.Builder;
import org.matsim.contrib.freight.jsprit.NetworkRouter;
import org.matsim.contrib.freight.usecases.chessboard.CarrierScoringFunctionFactoryImpl;
import org.matsim.contrib.freight.utils.FreightUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.router.costcalculators.TravelDisutilityFactory;
import org.matsim.core.router.util.LeastCostPathCalculator;
import org.matsim.core.router.util.LeastCostPathCalculatorFactory;
import org.matsim.core.router.util.TravelDisutility;
import org.matsim.core.router.util.TravelTime;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
 * @author ikaddoura
 */

public final class RunBerlinScenarioFreightServiceTests {

	private static final Id<Link> depotLinkId = Id.createLinkId("100575");
	private static final double vehicleStartTime = 3600 * 16; // 0
	private static final double vehicleEndTime = 0; // 36000
	private static final double serviceDuration = 0; // 300.0
	private static final double twStart = 0; // 3600.0
	private static final double twEnd = 0; // 36000.0
	
	private static final String scenarioConfig = "scenarios/berlin-v5.4-1pct/input/berlin-v5.4-1pct.config.xml";
//	private static final List<String> serviceOrder = new ArrayList<String>(Arrays.asList("2","4","5","8","10"));
	private static final List<String> serviceOrder = new ArrayList<String>(Arrays.asList("19499","195","5503","55028","128795"));


	
	
	private static final Logger log = Logger.getLogger(RunBerlinScenarioFreightServiceTests.class);
	private static ScheduledTour createTour(CarrierVehicle carrierVehicle, double tourDepTime,
			List<CarrierService> shipments) {
		
		Tour.Builder tourBuilder = Tour.Builder.newInstance();
		tourBuilder.scheduleStart(carrierVehicle.getLocation());
		for (CarrierService shipment : shipments) {
			tourBuilder.addLeg(new Leg());
			tourBuilder.scheduleService(shipment);
		}
		tourBuilder.addLeg(new Leg());
		tourBuilder.scheduleEnd(carrierVehicle.getLocation());

		org.matsim.contrib.freight.carrier.Tour vehicleTour = tourBuilder.build();
		ScheduledTour sTour = ScheduledTour.newInstance(vehicleTour, carrierVehicle, tourDepTime);
		assert sTour.getTour().getStartLinkId() == sTour.getTour().getEndLinkId();
		return sTour;
	}

	public static void main(String[] args) {

		for (String arg : args) {
			log.info(arg);
		}

		if (args.length == 0) {
			args = new String[] { scenarioConfig };
		}

		Config config = prepareConfig(args);
		config.controler().setLastIteration(0);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		Scenario scenario = prepareScenario(config);
		Controler controler = prepareControler(scenario);

		Carriers carriers = FreightUtils.getOrCreateCarriers(scenario);
		CarrierVehicle vehicle = createCarrierVehicle(scenario);
		List<CarrierService> shipments = createCarrierShipments();
		Carrier carrier = createCarrier(vehicle, shipments);
		carriers.addCarrier(carrier);
		
		
		CarrierCapabilities.Builder ccBuilder = CarrierCapabilities.Builder.newInstance().addType(vehicle.getType())
				.addVehicle(vehicle).setFleetSize(FleetSize.FINITE);
		carrier.setCarrierCapabilities(ccBuilder.build());

	

		Collection<ScheduledTour> scheduledTours = new ArrayList<ScheduledTour>();															// Cases raus.
		scheduledTours.add(createTour(vehicle, vehicle.getEarliestStartTime(), shipments));		
		CarrierPlan carrierPlan = new CarrierPlan(carrier, scheduledTours);
		carrierPlan.setScore((double) (999 * (-1)));

		Collection<VehicleType> vehicleTypes = new ArrayList<VehicleType>();
		vehicleTypes.add(vehicle.getType());
		Builder netBuilder = NetworkBasedTransportCosts.Builder.newInstance(scenario.getNetwork(), vehicleTypes );
		final NetworkBasedTransportCosts netBasedCosts = netBuilder.build();
		
		
		NetworkRouter.routePlan(carrierPlan, netBasedCosts);
		carrier.setSelectedPlan(carrierPlan);


		// --------- now register freight and start a MATsim run:
		controler.addOverridingModule(new CarrierModule());

		//this is necessary for replanning and scoring of carriers. will be replaced in future..
		controler.addOverridingModule(new AbstractModule() {
			@Override
			public void install() {
				bind( CarrierPlanStrategyManagerFactory.class ).toInstance( () -> null );
				bind( CarrierScoringFunctionFactory.class ).to( CarrierScoringFunctionFactoryImpl.class  ) ;
			}
		});
		controler.run();

	}
	
	private static List<CarrierService> createCarrierShipments() {
		List<CarrierService> shipments = new ArrayList<CarrierService>();
		
		int counter = 0;
		for (String service : serviceOrder) {
			shipments.add(createService("s"+ counter, service, serviceDuration, twStart, twEnd));
			counter++;
		}
		return shipments;
	}

	private static Carrier createCarrier(CarrierVehicle carrierVehicle, List<CarrierService> shipments) {
		Carrier carrierWShipments = CarrierUtils.createCarrier(Id.create("carrier", Carrier.class));
		for (CarrierService shipment : shipments)
			CarrierUtils.addService(carrierWShipments, shipment);

		// capabilities -> assign vehicles or vehicle types to carrier
		CarrierCapabilities.Builder ccBuilder = CarrierCapabilities.Builder.newInstance()
				.addType(carrierVehicle.getType()).addVehicle(carrierVehicle).setFleetSize(FleetSize.FINITE);
		carrierWShipments.setCarrierCapabilities(ccBuilder.build());

		return carrierWShipments;
	}

	private static CarrierService createService(String id, String from, double serviceDuration, double twStart, double twEnd) {
		Id<CarrierService> shipmentId = Id.create(id, CarrierService.class);
		Id<Link> fromLinkId = null;

		if (from != null) {
			fromLinkId = Id.create(from, Link.class);
		}

		return CarrierService.Builder.newInstance(shipmentId, fromLinkId).setServiceDuration(serviceDuration)
				.setServiceStartTimeWindow(TimeWindow.newInstance(twStart, twEnd)).build();
	}

	private static CarrierVehicle createCarrierVehicle(Scenario scenario) {

		// Create vehicle type
		VehicleType carrierVehType = createCarrierVehType();
		CarrierVehicleTypes vehicleTypes = FreightUtils.getCarrierVehicleTypes(scenario); // create CarrierVehicleTypes
																							// and register in
																							// scenario;;
		vehicleTypes.getVehicleTypes().put(carrierVehType.getId(), carrierVehType);

		// create vehicle
		CarrierVehicle carrierVehicle = CarrierVehicle.Builder
				.newInstance(Id.create("gridVehicle", org.matsim.vehicles.Vehicle.class), depotLinkId)
				.setEarliestStart(vehicleStartTime).setLatestEnd(vehicleEndTime).setType(carrierVehType).build();

		return carrierVehicle;
	}

	private static VehicleType createCarrierVehType() {
		// m/s
		VehicleType vehicleType = VehicleUtils.createVehicleType(Id.create("gridType", VehicleType.class));
		vehicleType.setMaximumVelocity(10); // in m/s
		vehicleType.getCapacity().setOther(5.);
		vehicleType.getCostInformation().setCostsPerMeter(0.0001);
		vehicleType.getCostInformation().setCostsPerSecond(0.001);
		vehicleType.getCostInformation().setFixedCost((double) 130);
		VehicleUtils.setHbefaTechnology(vehicleType.getEngineInformation(), "diesel");
		VehicleUtils.setFuelConsumption(vehicleType, 0.015);
		return vehicleType;
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
