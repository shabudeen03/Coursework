function initialize() {
  formValue = document.getElementById("formInput");
  logOutput = document.getElementById("logform");
  groceryOutput = document.getElementById("list");
  array = [];
  itemShifter = "";
  itemChecker = document.getElementById("itemCheck");

  display();
}

function updateList(object, condition) {
  if (object != "") itemShifter = object;
  if (condition == "0") addItem(object);
  if (condition == "1") deleteItem(object);
  if (condition == "00") moveUp(object);
  if (condition == "11") moveDown(object);

  display();
}

function addItem(parameter) {
  parameter.toLowerCase();

  if (array.indexOf(parameter) == -1) {
    array.push(parameter);
    itemChecker.innerHTML = "";
  }

  else itemChecker.innerHTML = "Item is already added to the list.";
}

function deleteItem(parameter) { if (parameter >= 1 && parameter <= (array.length + 1))  array.splice((parameter-1), 1); }

function shift(shifter) {
  var stored = array[itemShifter-shifter];
  array[itemShifter-shifter] = array[itemShifter-1];
  array[itemShifter-1] = stored;
  if (shifter == 2) itemShifter --;
  else itemShifter ++;
}

function moveUp(parameter) {
  if (parameter == "")  if (itemShifter > 1 && itemShifter <= (array.length))  shift(2);
  if (parameter != "")  if (itemShifter > 1 && itemShifter <= (array.length))  shift(2);
}

function moveDown(parametr) {
  if (parametr == "")  if (itemShifter >= 1 && itemShifter < (array.length))  shift(0);
  if (parametr != "")  if (itemShifter >= 1 && itemShifter < (array.length))  shift(0);
}

function display(object) {
  if (array.length != 0) {
    logOutput.innerHTML="1: "+array[0];

    for (var i=1; i < array.length; i++) {
      var x = i + 1;
      logOutput.innerHTML+="<br />"+x+": "+ array[i];
    }
  }

  else logOutput.innerHTML = "";

  formValue.logInput.value = "";
}
