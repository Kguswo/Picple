importScripts('https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1632777926/selfie_segmentation.js');

let selfieSegmentation;

self.onmessage = async function(e) {
  if (e.data.type === 'init') {
    selfieSegmentation = new self.SelfieSegmentation({locateFile: (file) => {
      return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`;
    }});
    selfieSegmentation.setOptions({
      modelSelection: 1,
    });
    await selfieSegmentation.initialize();
    self.postMessage({ type: 'ready' });
  } else if (e.data.type === 'segment') {
    const results = await selfieSegmentation.send({image: e.data.imageData});
    self.postMessage({ type: 'segmentation', segmentation: results.segmentationMask }, [results.segmentationMask]);
  }
};