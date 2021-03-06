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

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.TransportMode;
import org.matsim.api.core.v01.network.Link;
import org.matsim.contrib.freight.carrier.Carrier;
import org.matsim.contrib.freight.carrier.CarrierCapabilities;
import org.matsim.contrib.freight.carrier.CarrierCapabilities.FleetSize;
import org.matsim.contrib.freight.carrier.CarrierPlan;
import org.matsim.contrib.freight.carrier.CarrierPlanXmlWriterV2;
import org.matsim.contrib.freight.carrier.CarrierShipment;
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
import org.matsim.contrib.freight.usecases.analysis.CarrierScoreStats;
import org.matsim.contrib.freight.usecases.analysis.LegHistogram;
import org.matsim.contrib.freight.usecases.chessboard.CarrierScoringFunctionFactoryImpl;
import org.matsim.contrib.freight.utils.FreightUtils;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup.ActivityParams;
import org.matsim.core.config.groups.QSimConfigGroup.TrafficDynamics;
import org.matsim.core.config.groups.VspExperimentalConfigGroup;
import org.matsim.core.controler.AbstractModule;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.OutputDirectoryLogging;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.controler.OutputDirectoryHierarchy.OverwriteFileSetting;
import org.matsim.core.gbl.Gbl;
import org.matsim.core.scenario.ScenarioUtils;
import org.matsim.vehicles.VehicleType;
import org.matsim.vehicles.VehicleUtils;

import ch.sbb.matsim.routing.pt.raptor.SwissRailRaptorModule;

/**
 * @author ikaddoura
 */

public final class RunBerlinScenarioFreightTests {

	private static final Logger log = Logger.getLogger(RunBerlinScenarioFreightTests.class);
	private static final Id<Link> depotLinkId = Id.createLinkId("1");

	// define shipments
	private static List<CarrierShipment> createCarrierShipments() {
		List<CarrierShipment> shipments = new ArrayList<CarrierShipment>();
		shipments.add(createShipment("1", "2", "2", 1));
		shipments.add(createShipment("2", "4", "4", 1));
		shipments.add(createShipment("3", "6", "6", 1));
		shipments.add(createShipment("4", "8", "8", 1));
		shipments.add(createShipment("5", "10", "10", 1));
		return shipments;
	}

	private static ScheduledTour createTour(CarrierVehicle carrierVehicle, double tourDepTime,
			List<CarrierShipment> shipments) {
		
		Tour.Builder tourBuilder = Tour.Builder.newInstance();
		tourBuilder.scheduleStart(depotLinkId);
		for (CarrierShipment shipment : shipments) {
			tourBuilder.addLeg(new Leg());
			tourBuilder.schedulePickup(shipment);
			tourBuilder.addLeg(new Leg());
			tourBuilder.scheduleDelivery(shipment);
		}
		tourBuilder.addLeg(new Leg());
		tourBuilder.scheduleEnd(depotLinkId);

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
			args = new String[] { "scenarios/equil/config.xml" };
		}

		Config config = prepareConfig(args);
		config.controler().setLastIteration(0);
		config.controler().setOverwriteFileSetting(OverwriteFileSetting.deleteDirectoryIfExists);
		Scenario scenario = prepareScenario(config);
		Controler controler = prepareControler(scenario);

//		final Carriers carriers = FreightUtils.getCarriers(scenario);
//		new CarrierPlanXmlReader(carriers).readURL(IOUtils.extendUrl(config.getContext(), "carrier/singleCarrier.xml"));

		Carriers carriers = FreightUtils.getOrCreateCarriers(scenario);
		CarrierVehicle vehicle = createCarrierVehicle(scenario);
		List<CarrierShipment> shipments = createCarrierShipments();
		Carrier carrier = createCarrier(vehicle, shipments);
		carriers.addCarrier(carrier);

//		final CarrierVehicleTypes types = new CarrierVehicleTypes();
//		new CarrierVehicleTypeReader(types).readURL(IOUtils.extendUrl(config.getContext(), "carrier/vehicleTypes.xml"));
//		new CarrierVehicleTypeLoader(carriers).loadVehicleTypes(types);
		
		
		CarrierCapabilities.Builder ccBuilder = CarrierCapabilities.Builder.newInstance().addType(vehicle.getType())
				.addVehicle(vehicle).setFleetSize(FleetSize.FINITE);
		carrier.setCarrierCapabilities(ccBuilder.build());

	

		Collection<ScheduledTour> scheduledTours = new ArrayList<ScheduledTour>();															// Cases raus.
		scheduledTours.add(createTour(vehicle, 0, shipments));		
		CarrierPlan carrierPlan = new CarrierPlan(carrier, scheduledTours);
		carrierPlan.setScore((double) (999 * (-1)));

		Collection<VehicleType> vehicleTypes = new ArrayList<VehicleType>();
		vehicleTypes.add(vehicle.getType());
		Builder netBuilder = NetworkBasedTransportCosts.Builder.newInstance(scenario.getNetwork(), vehicleTypes );
//		Builder netBuilder = NetworkBasedTransportCosts.Builder.newInstance(scenario.getNetwork());
		final NetworkBasedTransportCosts netBasedCosts = netBuilder.build();
		NetworkRouter.routePlan(carrierPlan, netBasedCosts);
		carrier.setSelectedPlan(carrierPlan);

		new CarrierPlanXmlWriterV2(carriers)
				.write(config.controler().getOutputDirectory() + "/servicesAndShipments_plannedCarriers.xml");


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
//		Freight.configure(controler);
//		FreightConfigGroup freightConfig = ConfigUtils.addOrGetModule( config, FreightConfigGroup.class );;
//		freightConfig.setTimeWindowHandling(FreightConfigGroup.TimeWindowHandling.enforceBeginnings);
//		addFreightConfig(config, controler, carriers);
		controler.run();

	}

	private static Carrier createCarrier(CarrierVehicle carrierVehicle, List<CarrierShipment> shipments) {
		Carrier carrierWShipments = CarrierUtils.createCarrier(Id.create("carrier", Carrier.class));
		// TODO: Geht derzeit nur als "int" für ovgu... kmt/aug19
		for (CarrierShipment shipment : shipments)
			CarrierUtils.addShipment(carrierWShipments, shipment);

		// capabilities -> assign vehicles or vehicle types to carrier
		CarrierCapabilities.Builder ccBuilder = CarrierCapabilities.Builder.newInstance()
				.addType(carrierVehicle.getType()).addVehicle(carrierVehicle).setFleetSize(FleetSize.FINITE);
		carrierWShipments.setCarrierCapabilities(ccBuilder.build());

		return carrierWShipments;
	}

	private static CarrierShipment createShipment(String id, String from, String to, int size) {
		Id<CarrierShipment> shipmentId = Id.create(id, CarrierShipment.class);
		Id<Link> fromLinkId = null;
		Id<Link> toLinkId = null;

		if (from != null) {
			fromLinkId = Id.create(from, Link.class);
		}
		if (to != null) {
			toLinkId = Id.create(to, Link.class);
		}

		return CarrierShipment.Builder.newInstance(shipmentId, fromLinkId, toLinkId, size).setDeliveryServiceTime(30.0)
				.setDeliveryTimeWindow(TimeWindow.newInstance(3600.0, 36000.0)).setPickupServiceTime(5.0)
				.setPickupTimeWindow(TimeWindow.newInstance(0.0, 7200.0)).build();
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
				.setEarliestStart(0.0).setLatestEnd(36000.0).setType(carrierVehType).build();

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

	private static void addFreightConfig(Config config, Controler controler, Carriers carriers) {

//        controler.addOverridingModule(new AbstractModule() {
//            @Override
//            public void install() {
//                //                CarrierModule carrierModule = new CarrierModule(carriers);
//                //                carrierModule.setPhysicallyEnforceTimeWindowBeginnings(true);
//                //                install(carrierModule);
//                bind(CarrierPlanStrategyManagerFactory.class).toInstance( new MyCarrierPlanStrategyManagerFactory(types) );
//                bind(CarrierScoringFunctionFactory.class).toInstance( new MyCarrierScoringFunctionFactory() );
//            }
//        });
		controler.addOverridingModule(new AbstractModule() {

			@Override
			public void install() {
				final CarrierScoreStats scores = new CarrierScoreStats(carriers,
						config.controler().getOutputDirectory() + "/carrier_scores", true);
				final int statInterval = 1;
				final LegHistogram freightOnly = new LegHistogram(900);
				freightOnly.setInclPop(false);
				binder().requestInjection(freightOnly);
				final LegHistogram withoutFreight = new LegHistogram(900);
				binder().requestInjection(withoutFreight);

				addEventHandlerBinding().toInstance(withoutFreight);
				addEventHandlerBinding().toInstance(freightOnly);
				addControlerListenerBinding().toInstance(scores);
				addControlerListenerBinding().toInstance(new IterationEndsListener() {

					@Inject
					private OutputDirectoryHierarchy controlerIO;

					@Override
					public void notifyIterationEnds(IterationEndsEvent event) {
						if (event.getIteration() % statInterval != 0)
							return;
						// write plans
						String dir = controlerIO.getIterationPath(event.getIteration());
						new CarrierPlanXmlWriterV2(carriers)
								.write(dir + "/" + event.getIteration() + ".singleCarrier.xml");

						// write stats
						freightOnly.writeGraphic(dir + "/" + event.getIteration() + ".legHistogram_freight.png");
						freightOnly.reset(event.getIteration());

						withoutFreight
								.writeGraphic(dir + "/" + event.getIteration() + ".legHistogram_withoutFreight.png");
						withoutFreight.reset(event.getIteration());
					}
				});
			}
		});

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
