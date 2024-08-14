let selfieSegmentation;

async function fetchAndCreateBlobUrl(url) {
  const response = await fetch(url);
  const blob = await response.blob();
  return URL.createObjectURL(blob);
}

self.onmessage = async function(e) {
  if (e.data.type === 'init') {
    try {
      const scriptUrl = await fetchAndCreateBlobUrl('https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/selfie_segmentation.js');
      importScripts(scriptUrl);

      selfieSegmentation = new self.SelfieSegmentation({locateFile: (file) => {
        return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/${file}`;
      }});

      selfieSegmentation.setOptions({
        modelSelection: 1,
      });

      await selfieSegmentation.initialize();
      self.postMessage({ type: 'ready' });
    } catch (error) {
      self.postMessage({ type: 'error', message: error.message });
    }
  } else if (e.data.type === 'segment') {
    try {
      const results = await selfieSegmentation.send({image: e.data.imageData});
      self.postMessage({ type: 'segmentation', segmentation: results.segmentationMask }, [results.segmentationMask]);
    } catch (error) {
      self.postMessage({ type: 'error', message: error.message });
    }
  }
};