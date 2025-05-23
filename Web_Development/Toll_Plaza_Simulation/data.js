				//arrays w/4 indexes or index of an array w/4 indexes have format: car, truck, motor, bus
const OFFSET = -1, EZ_DISCOUNT = 0.9;
const RESIDENTIAL = [0.6, 1.0, 0.6, 1.0]; // probability of being residential
const RESIDENTIAL_DISCOUNT = [0.6, 0.9, 0.6, 1.0]; //how much (%) of the actual toll is paid if residential
const EZ_PASS = [0.7, 0.9, 0.7, 1.0]; // probability of attaining ez-pass
const TOLL = [12, 18, 8, 0];
	//Batch format: [start time, end time, [%car, %truck, %motorcycle, %bus], [minVehicle, maxVehicle]]
const BATCH = [[0, 300, [0.3, 0.65, 0.05, 0], [2, 10]],
			   			[301, 540, [0.5, 0.25, 0.05, 0.2], [20, 40]],
			   			[541, 840, [0.4, 0.4, 0.1, 0.1], [5, 20]],
			   			[841, 960, [0.45, 0.3, 0.1, 0.15], [10, 30]],
			   			[961, 1080, [0.6, 0.15, 0.05, 0.2], [20, 40]],
			   			[1081, 1200, [0.6, 0.25, 0.05, 0.1], [10, 30]],
			   			[1201, 1439, [0.5, 0.4, 0.05, 0.05], [5, 15]]];

function initialize() {
	ct1 = document.getElementById("0"); ct2 = document.getElementById("1"); ct3 = document.getElementById("2"); ct4 = document.getElementById("3"); //counts of vehicle types
	ct1ez = document.getElementById("4"); ct2ez = document.getElementById("5"); ct3ez = document.getElementById("6"); ct4ez = document.getElementById("7"); //counts of those w/ EZ-Pass
	ct1res = document.getElementById("8"); ct2res = document.getElementById("9"); ct3res = document.getElementById("10"); ct4res = document.getElementById("11"); // counts of residential

	totalEzOutput = document.getElementById("totalEzPass"); totalPlateOutput = document.getElementById("totalPlate"); //totals
	ezPercent = document.getElementById("percentEZ"); platePercent = document.getElementById("percentPlate"); //% of totals
	infoState = document.getElementById("infoPopup"); form = document.getElementById("formInput"); //fixed display
	countDisplay = document.getElementById("countId"); log = document.getElementById("logOutput"); dataTable = document.getElementById("data"); //malleable display
	
	displayArr = [[ct1, ct2, ct3, ct4], [ct1ez, ct2ez, ct3ez, ct4ez], [ct1res, ct2res, ct3res, ct4res]]; //mkaes it easier to loop through for displaying values for the way I coded
	for(var i=0; i<3; i++) for(var n=0; n<4; n++) displayArr[i][n].innerHTML = 0;	
	counter = 0; //timer
	speed = undefined; simulation = false;
	via_EZ_Pass = 0; via_Pay_Plate = 0; 
	counterTimer = OFFSET;
	display([]);
}

function count() {
	if(counter == OFFSET) counter = 0;
	var zone = checkTimeZone(); //time zone for using the respective index of the BATCH
	var second = new GenerateSecond(zone); //creates the num vehicles for each second depending on the time (zone)
	second.determineStatus(); // gets status for residential and ex-pass
	second.getToll(); //gets toll using the status from previous line
	counter++; //time continues
	display(second);
}

function GenerateSecond(period) {
	var total = getRandomInteger(BATCH[period][3][0], BATCH[period][3][1]); //total vehicles randomly gotten by the two bounds from given conditions
	this.vehicles = [0, 0, 0, 0]; //stores total of each type of vehicle for one second, same 4 index format as constants
	var checkTransfer = 0; //for checking if all the vehicles from the types adds up to the randomly generated total
	for(var i=0; i<4; i++) {
		if(checkTransfer >= total) total = 0; // in case the preceding values add up to more than the total generated vehicles
		this.vehicles[i] += parseInt(total * BATCH[period][2][i]); //uses percentage from the batch to add on to each type of vehicle
		checkTransfer += this.vehicles[i];
		if(i == 3 && checkTransfer < total) { //if vehicles gotten through % add up to less than the given total amount, then it redistributes the remaining again
			if(period != 0) {
				this.vehicles[getRandomInteger(0, 3)] += total - checkTransfer;
			} else this.vehicles[getRandomInteger(0, 2)] += total - checkTransfer; //due to one batch having the 0% for the bus
		}
	}
}

GenerateSecond.prototype.determineStatus = function() { //first 2 lines are the initialization for each second, next 2 uses the first 2 lines in an array suitable for the following loop
	/*I didn't think it was necessary to have on big array to keep on storing data to as this simulation did not require me to access any "individual" information, so I just went with this. 
	If there is a much simpler way than just this, please tell me. */
	this.carPass = []; this.truckPass = []; this.motorPass = []; this.busPass = [];
	this.resCar = []; this.resTruck = []; this.resMotor = []; this.resBus = [];
	this.ezPass = [this.carPass, this.truckPass, this.motorPass, this.busPass];
	this.residential = [this.resCar, this.resTruck, this.resMotor, this.resBus];
	for(var i=0; i<4; i++) {
		for(var n=0; n<this.vehicles[i]; n++) {
			this.residential[i][n] = " non-residential "; // set to non residential and only changes if randomly gets residential
			if(Math.random() <= EZ_PASS[i]) {
				this.ezPass[i][n] = " with EZ-Pass ";
				if(Math.random() <= RESIDENTIAL[i]) this.residential[i][n] = " residential ";
			}
			else this.ezPass[i][n] = " without EZ-Pass ";
		}
	}
};

GenerateSecond.prototype.getToll = function() { //first line  is the initialization for each second, next line uses the first line in an array suitable for the following loop
	this.carToll = []; this.truckToll = []; this.motorToll = []; this.busToll = [];
	this.toll = [this.carToll, this.truckToll, this.motorToll, this.busToll];
	for(var i=0; i<4; i++) {
		for(var n=0; n<this.vehicles[i]; n++) {
			if(this.ezPass[i][n] == " with EZ-Pass ") {
				this.toll[i][n] = Math.round((TOLL[i] * EZ_DISCOUNT)*100)/100;
				if(this.residential[i][n] == " residential ") this.toll[i][n] = Math.round((TOLL[i] * RESIDENTIAL_DISCOUNT[i])*100)/100;
			}
			else this.toll[i][n] = TOLL[i];
		}
	}
};

function startCounter() { 
	if(speed == undefined) alert("Please pick a speed");
	else if(counterTimer == OFFSET) {
		counterTimer = setInterval(count, 1000 * speed); //form value is inverted which is why it's multiplying
		simulation = true;
	}
}

function setSpeed(spd) {
	if(simulation == true) alert("Please pause the simulation, then change the speed");
	else speed = spd;
}

function stopCounter(stopper) {
	simulation = false;
	clearInterval(counterTimer);
	if (stopper == 'stop') { //stopping simulation
		totalEzOutput.innerHTML = 0;
		totalPlateOutput.innerHTML = 0;
		log.innerHTML = "";
		initialize();
	}
	counterTimer = OFFSET;
}

function checkTimeZone() { //finds which time interval clock is in
	for (var i=0; i<BATCH.length; i++) if (BATCH[i][0] <= counter && counter <= BATCH[i][1]) return i;
}

function getRandomInteger(lower, upper) { //R = (rnd * (u - (L - 1)) + L
	multiplier = upper - (lower - 1);
	rnd = parseInt(Math.random() * multiplier) + lower;
	return rnd;
}

function toggleInstructions() { //open or close instructions
	if (infoState.style.display == "") infoState.style.display = "inline-block";
	else infoState.style.display = "";
}

function display(list) {
	countDisplay.innerHTML = "Time: " + counter;
	var type = ["Car", "Truck", "Motor Cycle", "Bus"];
	if(log.innerHTML == "") log.style.overflow = "hidden";
	for(var i in list.vehicles) if(list.vehicles[i] != 0) {
		log.style.overflow = "scroll";
		log.style.textAlign = "center";
		for(var n=0; n<list.vehicles[i]; n++) {
			displayArr[0][i].innerHTML++;
			log.innerHTML += "<br />" + "A" + list.residential[i][n] + type[i] + list.ezPass[i][n] + "paid $" + list.toll[i][n];
			if(list.ezPass[i][n] == " with EZ-Pass ") {
				via_EZ_Pass += list.toll[i][n];
				if(list.residential[i][n] == " residential ") displayArr[2][i].innerHTML++;//resCount[i]++;
				displayArr[1][i].innerHTML++;
			}
			else via_Pay_Plate += list.toll[i][n];
		}
	}
	if(log.innerHTML != "") log.innerHTML += "<br />"; //every minute is separated by a line
	if(via_EZ_Pass == 0 && via_Pay_Plate == 0) { //to solve "NaN" issue
		ezPercent.innerHTML = 0;
		platePercent.innerHTML = 0;
	}
	else {
		totalEzOutput.innerHTML = Math.round(via_EZ_Pass*100)/100;
		totalPlateOutput.innerHTML = via_Pay_Plate;
		ezPercent.innerHTML = Math.round((((Math.round(via_EZ_Pass*100)/100)/(Math.round(via_EZ_Pass*100)/100 + via_Pay_Plate))*100)*100)/100;
		platePercent.innerHTML = Math.round((100 - (Math.round((((Math.round(via_EZ_Pass*100)/100)/(Math.round(via_EZ_Pass*100)/100 + via_Pay_Plate))*100)*100)/100))*100)/100;
	}
}
