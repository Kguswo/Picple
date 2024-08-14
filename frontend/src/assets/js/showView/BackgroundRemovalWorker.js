importScripts('https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/selfie_segmentation.js');

let selfieSegmentation;

self.onmessage = async function(e) {
  if (e.data.type === 'init') {
    try {
      console.log('Initializing SelfieSegmentation');
      selfieSegmentation = new self.SelfieSegmentation({locateFile: (file) => {
        const url = `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation@0.1.1675465747/${file}`;
        console.log(`Loading file: ${url}`);
        return url;
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