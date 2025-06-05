-- Flight instance
CREATE INDEX flightinstance_flightnumber ON FlightInstance (FlightNumber);
CREATE INDEX flightinstance_flightnumber_date ON FlightInstance (FlightNumber, FlightDate);
CREATE INDEX flightinstance_flightdate ON FlightInstance (FlightDate);

-- Flight departures and landings
CREATE INDEX flight_departure ON Flight (DepartureCity);
CREATE INDEX flight_arrival ON Flight (ArrivalCity);
 
 -- Schedules
CREATE INDEX schedule_flightnumber ON Schedule (FlightNumber);
CREATE INDEX reservation_flightinstance ON Reservation (FlightInstanceID);

-- Maintenance
CREATE INDEX repair_planeid ON Repair (PlaneID);
CREATE INDEX repair_planeid_repairdate ON Repair (PlaneID, RepairDate);
CREATE INDEX repair_technicianid ON Repair (TechnicianID);
CREATE INDEX maintenancerequest_planeid ON MaintenanceRequest (PlaneID);
CREATE INDEX maintenancerequest_pilotid ON MaintenanceRequest (PilotID);