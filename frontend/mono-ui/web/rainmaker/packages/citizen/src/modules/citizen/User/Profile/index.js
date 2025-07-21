import React, { Component } from "react";
import { connect } from "react-redux";
import formHoc from "egov-ui-kit/hocs/form";
import { UploadDrawer } from "modules/common";
import ProfileForm from "./components/ProfileForm";
import { Screen } from "modules/common";
import img from "egov-ui-kit/assets/images/download.png";
import { fileUpload, removeFile } from "egov-ui-kit/redux/form/actions";
import "./index.css";

const ProfileFormHOC = formHoc({ formKey: "profile" })(ProfileForm);

class Profile extends Component {
  state = {
    openUploadSlide: false,
    localProfilePreview: null,
  };

  setProfilePic = (file = null, imageUri = "") => {
    const { fileUpload } = this.props;
    this.removeProfilePic();
    if (file) {
      const tempUrl = URL.createObjectURL(file);
      this.setState({ localProfilePreview: tempUrl });
    }

    fileUpload("profile", "photo", { module: "rainmaker-pgr", file, imageUri }, true);
  };

  removeProfilePic = () => {
    const { removeFile } = this.props;
    if (this.state.localProfilePreview) {
      URL.revokeObjectURL(this.state.localProfilePreview);
    }

    this.setState({ localProfilePreview: null });
    removeFile("profile", "photo", 0);
  };

  onClickAddPic = (isOpen) => {
    this.setState({
      openUploadSlide: isOpen,
    });
  };

  render() {
    const { profilePic, loading, userInfo } = this.props;
    const { openUploadSlide, localProfilePreview } = this.state;

    return (
      <Screen loading={loading} className="citizen-profile-screen">
        <div className="profile-container">
          <ProfileFormHOC onClickAddPic={this.onClickAddPic} img={img} profilePic={profilePic} localProfilePreview={localProfilePreview} userInfo={userInfo}/>
        </div>
        {openUploadSlide && (
          <UploadDrawer removeFile={this.removeProfilePic} setProfilePic={this.setProfilePic} onClickAddPic={this.onClickAddPic} openUploadSlide={openUploadSlide}/>
        )}
      </Screen>
    );
  }
}

const mapStateToProps = (state) => {
  const formKey = "profile";
  const form = state.form[formKey] || {};
  const userInfo = state.auth["userInfo"] || {};
  const images = (form && form.files && form.files["photo"]) || [];
  const loading = images.reduce((loading, file) => loading || file.loading, false);

  return {
    profilePic: (images.length && images[0].imageUri) || img,
    loading,
    userInfo,
  };
};

const mapDispatchToProps = (dispatch) => ({
  fileUpload: (formKey, fieldKey, module, fileObject) => dispatch(fileUpload(formKey, fieldKey, module, fileObject)),
  removeFile: (formKey, fieldKey, index) => dispatch(removeFile(formKey, fieldKey, index)),
});

export default connect(
  mapStateToProps, 
  mapDispatchToProps
  )(Profile);
