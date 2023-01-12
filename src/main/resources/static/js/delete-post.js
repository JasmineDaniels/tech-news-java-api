async function deleteFormHandler(event) {
  event.preventDefault();

  let dash_URL = '/dashboard';
  const id = window.location.toString().split('/')[
    window.location.toString().split('/').length - 1
  ];

   const response = await fetch('/api/posts/' + `${id}`, {
    method: 'DELETE',
    headers: {
          'Content-Type': 'application/json'
    }
  });

  if (response.ok) {
    window.location.href = dash_URL
  } else {
    alert(response.statusText);
  }
}

document.querySelector('.delete-post-btn').addEventListener('click', deleteFormHandler);