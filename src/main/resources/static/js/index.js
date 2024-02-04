'use strict';

const heygen_API = {
	//const apiKey = 'NjBmMDEyYzFjNTBlNDg2ZmIyYTQ2M2I3NGNlNWFmZmUtMTcwNzA2NTUyOA=='; // ktrophy
    //const apiKey = 'OWI4MzM3YTMxNTZhNGJjMzkwYWExZWFiOGI3ZmNlZDUtMTcwNzA2Njg2MA=='; // NB
        
  apiKey: 'OWI4MzM3YTMxNTZhNGJjMzkwYWExZWFiOGI3ZmNlZDUtMTcwNzA2Njg2MA==',
  serverUrl: 'https://api.heygen.com',
};

const statusElement = document.querySelector('#status');
const apiKey = heygen_API.apiKey;
const SERVER_URL = heygen_API.serverUrl;

if (apiKey === 'YourApiKey' || SERVER_URL === '') {
  alert('Please enter your API key and server URL in the api.json file');
}

let sessionInfo = null;
let peerConnection = null;

function updateStatus(statusElement, message) {
  statusElement.innerHTML += message + '<br>';
  statusElement.scrollTop = statusElement.scrollHeight;
}

updateStatus(statusElement, 'Please click the new button to create the stream first.');

function onMessage(event) {
  const message = event.data;
  console.log('Received message:', message);
}

async function createNewSession() {
  updateStatus(statusElement, 'Creating new session... please wait');

  const avatar = avatarName.value;
  const voiceIDElement = document.querySelector('#voiceID');
const voice = voiceIDElement.value;


  try {
    sessionInfo = await newSession('high', avatar, voice);
    const { sdp: serverSdp, ice_servers2: iceServers } = sessionInfo;

    peerConnection = new RTCPeerConnection({ iceServers: iceServers });

    peerConnection.onicecandidate = ({ candidate }) => {
      console.log('Received ICE candidate:', candidate);
      if (candidate) {
        handleICE(sessionInfo.session_id, candidate.toJSON());
      }
    };

    peerConnection.oniceconnectionstatechange = (event) => {
      updateStatus(
        statusElement,
        `ICE connection state changed to: ${peerConnection.iceConnectionState}`,
      );
    };

    peerConnection.ontrack = (event) => {
      console.log('Received the track');
      if (event.track.kind === 'audio' || event.track.kind === 'video') {
        mediaElement.srcObject = event.streams[0];
      }
    };

    peerConnection.ondatachannel = (event) => {
      const dataChannel = event.channel;
      dataChannel.onmessage = onMessage;
    };

    const remoteDescription = new RTCSessionDescription(serverSdp);
    await peerConnection.setRemoteDescription(remoteDescription);

    updateStatus(statusElement, 'Session creation completed');
    updateStatus(statusElement, 'Now.You can click the start button to start the stream');
  } catch (error) {
    console.error('Error creating session:', error);
    updateStatus(statusElement, 'Error creating session. Please check the console for details.');
  }
}

async function startAndDisplaySession() {
  if (!sessionInfo) {
    updateStatus(statusElement, 'Please create a connection first');
    return;
  }

  updateStatus(statusElement, 'Starting session... please wait');

  try {
    const localDescription = await peerConnection.createAnswer();
    await peerConnection.setLocalDescription(localDescription);

    await startSession(sessionInfo.session_id, localDescription);
    updateStatus(statusElement, 'Session started successfully');
  } catch (error) {
    console.error('Error starting session:', error);
    updateStatus(statusElement, 'Error starting session. Please check the console for details.');
  }
}

const taskInput = document.querySelector('#taskInput');

async function repeatHandler() {
  if (!sessionInfo) {
    updateStatus(statusElement, 'Please create a connection first');
    return;
  }

  updateStatus(statusElement, 'Sending task... please wait');
  const text = taskInput.value;

  if (text.trim() === '') {
    alert('Please enter a task');
    return;
  }

  try {
    const resp = await repeat(sessionInfo.session_id, text);
    updateStatus(statusElement, 'Task sent successfully');
  } catch (error) {
    console.error('Error sending task:', error);
    updateStatus(statusElement, 'Error sending task. Please check the console for details.');
  }
}

async function closeConnectionHandler() {
  if (!sessionInfo) {
    updateStatus(statusElement, 'Please create a connection first');
    return;
  }

  renderID++;
  hideElement(canvasElement);
  hideElement(bgCheckboxWrap);
  mediaCanPlay = false;

  updateStatus(statusElement, 'Closing connection... please wait');

  try {
    peerConnection.close();
    const resp = await stopSession(sessionInfo.session_id);

    console.log(resp);
  } catch (error) {
    console.error('Failed to close the connection:', error);
    updateStatus(statusElement, 'Error closing connection. Please check the console for details.');
  }

  updateStatus(statusElement, 'Connection closed successfully');
}

document.querySelector('#newBtn').addEventListener('click', createNewSession);
document.querySelector('#startBtn').addEventListener('click', startAndDisplaySession);
document.querySelector('#repeatBtn').addEventListener('click', repeatHandler);
document.querySelector('#closeBtn').addEventListener('click', closeConnectionHandler);

async function newSession(quality, avatar_name, voice_id) {
  const response = await fetch(`${SERVER_URL}/v1/streaming.new`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Api-Key': apiKey,
    },
    body: JSON.stringify({
      quality,
      avatar_name,
      voice: {
        voice_id: voice_id,
      },
    }),
  });

  if (response.status === 500) {
    console.error('Server error');
    updateStatus(
      statusElement,
      'Server Error. Please ask the staff if the service has been turned on',
    );

    throw new Error('Server error');
  } else {
    const data = await response.json();
    console.log(data.data);
    return data.data;
  }
}

async function startSession(session_id, sdp) {
  const response = await fetch(`${SERVER_URL}/v1/streaming.start`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Api-Key': apiKey,
    },
    body: JSON.stringify({ session_id, sdp }),
  });

  if (response.status === 500) {
    console.error('Server error');
    updateStatus(
      statusElement,
      'Server Error. Please ask the staff if the service has been turned on',
    );
    throw new Error('Server error');
  } else {
    const data = await response.json();
    return data.data;
  }
}

async function handleICE(session_id, candidate) {
  const response = await fetch(`${SERVER_URL}/v1/streaming.ice`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Api-Key': apiKey,
    },
    body: JSON.stringify({ session_id, candidate }),
  });

  if (response.status === 500) {
    console.error('Server error');
    updateStatus(
      statusElement,
      'Server Error. Please ask the staff if the service has been turned on',
    );
    throw new Error('Server error');
  } else {
    const data = await response.json();
    return data;
  }
}

async function repeat(session_id, text) {
  const response = await fetch(`${SERVER_URL}/v1/streaming.task`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Api-Key': apiKey,
    },
    body: JSON.stringify({ session_id, text }),
  });

  if (response.status === 500) {
    console.error('Server error');
    updateStatus(
      statusElement,
      'Server Error. Please ask the staff if the service has been turned on',
    );
    throw new Error('Server error');
  } else {
    const data = await response.json();
    return data.data;
  }
}

async function stopSession(session_id) {
  const response = await fetch(`${SERVER_URL}/v1/streaming.stop`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Api-Key': apiKey,
    },
    body: JSON.stringify({ session_id }),
  });

  if (response.status === 500) {
    console.error('Server error');
    updateStatus(statusElement, 'Server Error. Please ask the staff for help');
    throw new Error('Server error');
  } else {
    const data = await response.json();
    return data.data;
  }
}

const removeBGCheckbox = document.querySelector('#removeBGCheckbox');
removeBGCheckbox.addEventListener('click', () => {
  const isChecked = removeBGCheckbox.checked;

  if (isChecked && !sessionInfo) {
    updateStatus(statusElement, 'Please create a connection first');
    removeBGCheckbox.checked = false;
    return;
  }

  if (isChecked && !mediaCanPlay) {
    updateStatus(statusElement, 'Please wait for the video to load');
    removeBGCheckbox.checked = false;
    return;
  }

  if (isChecked) {
    hideElement(mediaElement);
    showElement(canvasElement);

    renderCanvas();
  } else {
    hideElement(canvasElement);
    showElement(mediaElement);

    renderID++;
  }
});

let renderID = 0;
function renderCanvas() {
  if (!removeBGCheckbox.checked) return;
  hideElement(mediaElement);
  showElement(canvasElement);

  canvasElement.classList.add('show');

  const curRenderID = Math.trunc(Math.random() * 1000000000);
  renderID = curRenderID;

  const ctx = canvasElement.getContext('2d', { willReadFrequently: true });

  if (bgInput.value) {
    canvasElement.parentElement.style.background = bgInput.value?.trim();
  }

  function processFrame() {
    if (!removeBGCheckbox.checked) return;
    if (curRenderID !== renderID) return;

    canvasElement.width = mediaElement.videoWidth;
    canvasElement.height = mediaElement.videoHeight;

    ctx.drawImage(mediaElement, 0, 0, canvasElement.width, canvasElement.height);
    ctx.getContextAttributes().willReadFrequently = true;
    const imageData = ctx.getImageData(0, 0, canvasElement.width, canvasElement.height);
    const data = imageData.data;

    for (let i = 0; i < data.length; i += 4) {
      const red = data[i];
      const green = data[i + 1];
      const blue = data[i + 2];

      if (isCloseToGreen([red, green, blue])) {
        data[i + 3] = 0;
      }
    }

    ctx.putImageData(imageData, 0, 0);

    requestAnimationFrame(processFrame);
  }

  processFrame();
}

function isCloseToGreen(color) {
  const [red, green, blue] = color;
  return green > 90 && red < 90 && blue < 90;
}

function hideElement(element) {
  element.classList.add('hide');
  element.classList.remove('show');
}

function showElement(element) {
  element.classList.add('show');
  element.classList.remove('hide');
}

const mediaElement = document.querySelector('#mediaElement');
let mediaCanPlay = false;
mediaElement.onloadedmetadata = () => {
  mediaCanPlay = true;
  mediaElement.play();

  showElement(bgCheckboxWrap);
};

const canvasElement = document.querySelector('#canvasElement');
const bgCheckboxWrap = document.querySelector('#bgCheckboxWrap');
const bgInput = document.querySelector('#bgInput');
bgInput.addEventListener('keydown', (e) => {
  if (e.key === 'Enter') {
    renderCanvas();
  }
});
