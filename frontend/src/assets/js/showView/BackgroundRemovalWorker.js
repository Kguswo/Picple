importScripts('https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/selfie_segmentation.js');

let selfieSegmentation;

async function loadTFLite(url) {
  const response = await fetch(url);
  const arrayBuffer = await response.arrayBuffer();
  return new Uint8Array(arrayBuffer);
}

self.onmessage = async function(e) {
  if (e.data.type === 'init') {
    try {
      selfieSegmentation = new self.SelfieSegmentation({locateFile: async (file) => {
        if (file.endsWith('.tflite')) {
          const tfliteUrl = `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/${file}`;
          const tfliteData = await loadTFLite(tfliteUrl);
          return URL.createObjectURL(new Blob([tfliteData.buffer], {type: 'application/octet-stream'}));
        }
        return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/${file}`;
      }});

      selfieSegmentation.setOptions({
        modelSelection: 1,
      });

      await selfieSegmentation.initialize();
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