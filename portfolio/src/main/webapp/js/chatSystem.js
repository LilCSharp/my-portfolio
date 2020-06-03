function getTime() {
    
}

function loadTasks() {
  fetch('/data').then(response => response.json()).then((texts) => {
    const taskListElement = document.getElementById('comments');
    texts.forEach((words) => {
      taskListElement.appendChild(createTaskElement(words));
    })
  });
}

/** Creates an element that represents a task, including its delete button. */
function createTaskElement(words) {
  const wordsElement = document.createElement('ul');
  wordsElement.className = 'words';
  wordsElement.className = 'list-center'

  const textElement = document.createElement('span');
  textElement.innerText = words.text + "\n" + "\n";

  const timeElement = document.createElement('span');
  timeElement.innerText = words.date + "\n";

  const nameElement = document.createElement('span');
  nameElement.innerText = words.name + "\n";

  wordsElement.appendChild(nameElement);
  wordsElement.appendChild(timeElement);
  wordsElement.appendChild(textElement);

  return wordsElement;
}