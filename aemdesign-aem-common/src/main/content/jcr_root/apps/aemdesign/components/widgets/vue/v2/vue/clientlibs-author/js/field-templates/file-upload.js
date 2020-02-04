window.AEMDESIGN = window.AEMDESIGN || {};
window.AEMDESIGN.components = AEMDESIGN.components || {};
window.AEMDESIGN.components.authoring = AEMDESIGN.components.authoring || {};
window.AEMDESIGN.components.authoring.vue = AEMDESIGN.components.authoring.vue || {};
window.AEMDESIGN.components.authoring.vue.fields = AEMDESIGN.components.authoring.vue.fields || {};

((ns, undefined) => { // NOSONAR convention for wrapping all modules

  ns.fileUpload = (config, fieldPath, fieldPathBase, labelledBy, savedValue, componentPath) => {
    const fileUploadElement = document.createElement('div');
    const hasSelectedImage  = savedValue && savedValue['fileReference'];

    fileUploadElement.innerHTML = `
      <coral-fileupload class="coral-Form-field cq-FileUpload cq-droptarget"
        name="${fieldPath}"
        async
        data-foundation-validation
        accept="image"
        action="${componentPath}"
        data-cq-fileupload-temporaryfilename="${fieldPath}.sftmp"
        data-cq-fileupload-temporaryfiledelete="${fieldPath}.sftmp@Delete"
        data-cq-fileupload-temporaryfilepath="${componentPath}/${fieldPathBase}.sftmp">
        <div class="cq-FileUpload-thumbnail">
          <div class="cq-FileUpload-thumbnail-img" data-cq-fileupload-thumbnail-img>
            ${hasSelectedImage ? 
              `<img src="${savedValue['fileReference']}" alt="${savedValue['fileReference']}" title="${savedValue['fileReference']}">`
              : ''
            }
          </div>
          
          <button type="button" class="cq-FileUpload-clear" is="coral-button" variant="quiet" coral-fileupload-clear>Clear</button>
          
          <div class="cq-FileUpload-thumbnail-dropHere">
            <coral-icon icon="image" class="cq-FileUpload-icon"></coral-icon>
            <span class="cq-FileUpload-label">Drop an asset here.</span>
          </div>
        </div>
        
        <input type="hidden" name="${fieldPath}/fileName" data-cq-fileupload-parameter="filename" value="${(hasSelectedImage && savedValue['fileName']) || ''}" disabled>
        <input type="hidden" name="${fieldPath}/fileReference" data-cq-fileupload-parameter="filereference" value="${(hasSelectedImage && savedValue['fileReference']) || ''}" disabled>
        <input type="hidden" name="${fieldPath}@Delete" data-cq-fileupload-parameter="filedelete" disabled>
        <input type="hidden" name="${fieldPath}@MoveFrom" data-cq-fileupload-parameter="filemovefrom" value="${componentPath}/${fieldPathBase}.sftmp" disabled>
        <input type="hidden" name="${fieldPath}/jcr:lastModified">
        <input type="hidden" name="${fieldPath}/jcr:lastModifiedBy">
      </coral-fileupload>
    `;

    if (hasSelectedImage) {
      fileUploadElement.children[0].classList.add('is-filled')
    }

    return fileUploadElement;
  };

})(AEMDESIGN.components.authoring.vue.fields);
