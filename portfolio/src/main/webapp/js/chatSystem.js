var max = window.name;
var username = "none";

function submitEvent(event) {
  if (event.keyCode == 13) {
    max = document.getElementById("max").value;
    window.name = max.toString();
  }
}

function isInteger(value) {
  if(parseInt(value,10).toString() === value) {
    return true
  }
  return false;
}

function loadTasks() {
  
  console.log(max);

  if (!(isInteger(max))) {
    max = "5";
  }

  fetch('/data' + '?limit=' + max).then(response => response.json()).then((texts) => {
    const taskListElement = document.getElementById('comments');
    texts.forEach((words) => {
      taskListElement.appendChild(createTaskElement(words));
    })
  });
}

/** Creates an element that represents a task, including its delete button. */
function createTaskElement(words) {

  if (words.form == 0) {
    document.getElementById("input_form").style.visibility = "hidden";
    document.getElementById("log-in").innerText = "Log-In";
  } else {
    document.getElementById("input_form").style.visibility = "visible";
    document.getElementById("log-in").innerText = "Log-Out";
  }

  const wordsElement = document.createElement('p');
  wordsElement.className = 'words';
  wordsElement.className = 'list-center';

  const textElement = document.createElement('span');
  textElement.innerText = words.text + "\n";

  const timeElement = document.createElement('span');
  timeElement.innerText = words.date + "\n";

  const emailElement = document.createElement('span');
  emailElement.innerText = words.email + "\n";
  emailElement.setAttribute("id", words.id);

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteText(words);

    // Remove the task from the DOM.
    wordsElement.remove();
  }, () => {
    loadTasks();
  });

  //wordsElement.appendChild(nameElement);
  wordsElement.appendChild(emailElement);
  wordsElement.appendChild(timeElement);
  wordsElement.appendChild(textElement);
  wordsElement.appendChild(deleteButtonElement);

  if (words.imgURL == null) {    
    var photoHTML = "<input name=\"userEmail\" type=\"hidden\" value=\""
    photoHTML += words.email+"\"><input name=\"currentID\" type=\"hidden\" value=\""
    photoHTML += words.id+"\"></br><input type=\"file\" name=\"image\"><br/>";
    photoHTML += "<button>Submit</button>";
    const addPhotoElement = document.createElement('form');
    addPhotoElement.setAttribute("action", 'POST');
    addPhotoElement.innerHTML = photoHTML;
    wordsElement.appendChild(addPhotoElement);
  } else {
    const addPhotoElement = document.createElement('img');
    addPhotoElement.setAttribute("src", words.imgURL);
    addPhotoElement.setAttribute("alt", "");
    wordsElement.appendChild(addPhotoElement);
  }

  return wordsElement;
}

function deleteText(words) {
  const params = new URLSearchParams();
  params.append('id', words.id);
  fetch('/delete-task', {method: 'POST', body: params});
}