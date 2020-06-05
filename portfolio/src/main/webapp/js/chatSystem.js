var max = window.name;

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
  const wordsElement = document.createElement('p');
  wordsElement.className = 'words';
  wordsElement.className = 'list-center';

  const textElement = document.createElement('span');
  textElement.innerText = words.text + "\n";

  const timeElement = document.createElement('span');
  timeElement.innerText = words.date + "\n";

  const nameElement = document.createElement('span');
  nameElement.innerText = words.name + "\n";

  const deleteButtonElement = document.createElement('button');
  deleteButtonElement.innerText = 'Delete';
  deleteButtonElement.addEventListener('click', () => {
    deleteText(words);

    // Remove the task from the DOM.
    wordsElement.remove();
  }, () => {
    loadTasks();
  });

  wordsElement.appendChild(nameElement);
  wordsElement.appendChild(timeElement);
  wordsElement.appendChild(textElement);
  wordsElement.appendChild(deleteButtonElement);

  return wordsElement;
}

function deleteText(words) {
  const params = new URLSearchParams();
  params.append('id', words.id);
  fetch('/delete-task', {method: 'POST', body: params});
}