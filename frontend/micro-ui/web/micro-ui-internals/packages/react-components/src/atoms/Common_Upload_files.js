import React from 'react'

const CommonUploadFiles = (props) => {
  return (
     <div className="applicationWrapper">
              <div className="application-label">
                <label>{props.title}</label>
              </div>
              <div class="custom-file">
                <label class="custom-file-label" for="customFile">
                  <div>Choose File</div>
                  <input type="file" class="custom-file-input" id="customFile" />
                  <div className="upload-browse">Browse</div>
                </label>
              </div>
            </div> 
  )
}

export default CommonUploadFiles