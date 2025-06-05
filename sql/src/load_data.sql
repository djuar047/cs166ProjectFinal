/* Replace the location to where you saved the data files*/

\copy Plane FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Plane.csv' WITH DELIMITER ',' CSV HEADER; 

\copy Flight FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Flight.csv' WITH DELIMITER ',' CSV HEADER; 

\copy Schedule FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Schedule.csv' WITH DELIMITER ',' CSV HEADER; 

\copy FlightInstance FROM '/home/csmajs/djuar047/cs166_project_phase3/data/FlightInstance.csv' WITH DELIMITER ',' CSV HEADER; 

\copy Customer FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Customer.csv' WITH DELIMITER ',' CSV HEADER; 

\copy Reservation FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Reservation.csv' WITH DELIMITER ',' CSV HEADER; 

\copy Technician FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Technician.csv' WITH DELIMITER ',' CSV HEADER; 

\copy Repair FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Repair.csv' WITH DELIMITER ',' CSV HEADER; 

\copy Pilot FROM '/home/csmajs/djuar047/cs166_project_phase3/data/Pilot.csv' WITH DELIMITER ',' CSV HEADER; 

\copy MaintenanceRequest FROM '/home/csmajs/djuar047/cs166_project_phase3/data/MaintenanceRequest.csv' WITH DELIMITER ',' CSV HEADER; 

-- Duplicates error
SELECT setval('repair_repairid_seq', (SELECT MAX(RepairID) FROM Repair));
SELECT setval('maintenancerequest_requestid_seq', (SELECT COALESCE(MAX(requestid), 1) FROM MaintenanceRequest));
