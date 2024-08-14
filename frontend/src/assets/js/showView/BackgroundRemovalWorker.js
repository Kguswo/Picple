let selfieSegmentation;

async function loadLibrary() {
  const response = await fetch('https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/selfie_segmentation.js');
  const blob = await response.blob();
  const url = URL.createObjectURL(blob);
  importScripts(url);
  URL.revokeObjectURL(url);
}

self.onmessage = async function(e) {
  if (e.data.type === 'init') {
    try {
      await loadLibrary();
      console.log('MediaPipe library loaded');

      selfieSegmentation = new self.SelfieSegmentation({locateFile: (file) => {
        if (file.endsWith('.tflite')) {
          return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/${file}`;
        }
        return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/${file}`;
      }});

      selfieSegmentation.setOptions({
        modelSelection: 1,
      });

      await selfieSegmentation.initialize();
      console.log('SelfieSegmentation initialized successfully');
      self.postMessage({ type: 'ready' });
    } catch (error) {
      console.error('Initialization error:', error);
      self.postMessage({ type: 'error', message: error.message });
    }
  } else if (e.data.type === 'segment') {
    try {
      const results = await selfieSegmentation.send({image: e.data.imageData});
      self.postMessage({ type: 'segmentation', segmentation: results.segmentationMask }, [results.segmentationMask]);
    } catch (error) {
      console.error('Segmentation error:', error);
      self.postMessage({ type: 'error', message: error.message });
    }
  }
};