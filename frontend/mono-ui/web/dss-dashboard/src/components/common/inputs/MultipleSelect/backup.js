



// import React from 'react';
// import PropTypes from 'prop-types';
// import { withStyles } from '@material-ui/core/styles';
// import Input from '@material-ui/core/Input';
// import MenuItem from '@material-ui/core/MenuItem';
// import FormControl from '@material-ui/core/FormControl';
// import ListItemText from '@material-ui/core/ListItemText';
// import Select from '@material-ui/core/Select';
// import Divider from '@material-ui/core/Divider';
// import Checkbox from '@material-ui/core/Checkbox';
// import styles from './Styles';
// import _ from 'lodash';
// import SVG from 'react-inlinesvg';
// import districts_icon from '../../../../images/icon-districts.svg';
// import ulbs_icon from '../../../../images/icon-ul-bs.svg';

// class MultipleSelects1 extends React.Component {
//   constructor(props) {
//     super(props)
   
//     this.state = {
//       name: this.props.defaultValue || [],
//       search: '',
//       localItems: this.props.item || [],
//       options: [
//         {
//           text: "USA",
//           value: "1"
//         },
//         {
//           text: "Germany",
//           value: "2"
//         },
//         {
//           text: "France",
//           value: "3"
//         },
//         {
//           text: "Poland",
//           value: "4"
//         },
//         {
//           text: "Japan",
//           value: "5"
//         }
//       ]
//     };
//   }

//   handleChange = event => {
//     let { target } = this.props;
//     let newVals = _.compact(event.target.value);
//     if (newVals.length > 0) {
//       this.setState({ name: newVals });
//       this.props.handleSelected(false, target, newVals)
//     } else {
//       this.setState({ name: [] });
//       this.props.handleSelected(false, target, [])
//     }
//     this.props.handleClear()
//   };

//   // handleClose = event => {
//   //   if (typeof (this.props.handleClose) === 'function') {
//   //     this.props.handleClose(event.target.value)
//   //   }
//   // };
//   update_localItems(reverce) {
//     if (!reverce) {
//       this.setState({
//         localItems: _.filter(this.props.item, itm => {
//           return ((itm || '').toString().toLowerCase().indexOf((this.state.search || '').toLowerCase()) !== -1)
//         })

//       })
//     } else {
//       this.setState({
//         localItems: this.props.item
//       })
//     }
//   }
//   handleChanges_search(event) {
//     if (event.keyCode >= 65 && event.keyCode <= 90) {
//       this.setState({
//         search: this.state.search + event.key,
//       }, () => this.update_localItems(false));
//     } else {
//       this.setState({
//         search: '',
//       }, () => this.update_localItems(true));
//     }
//     event.target.focus()
//   }
//   onSearch(event, value) {
//   }
//   componentWillReceiveProps(nextProps) {
//     if (nextProps.defaultValue !== this.props.defaultValue) {
//       this.setState({
//         name: nextProps.defaultValue
//       });
//     }
//   }
//   renderMenues() {
//     const { classes } = this.props;
//     return (
//       <div>
//         {/* <div disableEnforceFocus={true} disableRipple component={"div"} autoFocus={true} className={classes.searchinput} >
//            <Input inputRef={input => input.focus()} onKeyDown={this.handleChanges_search.bind(this)} value={this.state.search} onChange={this.onSearch.bind(this)} />
//           <Divider color="#d9d9d9" />
//         </div> */}
//         {this.state.localItems.map((name, i) => (
//           <MenuItem tabIndex={i} component="div" ref={this.props.innerRef} disableRipple className={classes.menuItem} style={{ minWidth: (this.props.minWidth) ? this.props.minWidth : 200 }} color={'primary'} key={name} value={name}>
//             <ListItemText primary={name} />
//             <Checkbox checked={this.state.name.indexOf(name) > -1} />
//           </MenuItem>
//         ))}
//       </div>
//     )
//   }
//   render() {
//     const { classes, item, logo, target } = this.props;
//     let svgicon;
//     if (logo === "District") {
//       svgicon = districts_icon;
//     } else if (logo === "ULBS") {
//       svgicon = ulbs_icon;
//     }
//     return (
//       <div className={classes.root}>

//         <FormControl className={classes.formControl} >
//           {/* <InputLabel htmlFor="select-multiple-checkbox">{label || 'Select'}</InputLabel> */}
//           <div className={classes.list}>
//             <SVG src={svgicon} className={classes.CloseButton} >

//             </SVG>
//              <Select
//              multiple
//              disableUnderline={true}
//              value={this.props.clear ? [] : this.state.name}
//              onChange={this.handleChange.bind(this)}
//               displayEmpty={true}
//               renderValue={selected => selected.length > 0 ? +" " + selected.length + " Selected" : "All " + target}
//              MenuProps={{
//                className: classes.multiselectlist,
//              }}
//              disableEnforceFocus={true}
//              contain={true}
//              classes={{ root: classes.select, selectMenu: classes.menuItem }}

//             >
//                <div disableEnforceFocus={true} disableRipple component={"div"} autoFocus={true} className={classes.searchinput} >
//            <Input onKeyDown={this.handleChanges_search.bind(this)} value={this.state.search} onChange={this.onSearch.bind(this)} />
//           <Divider color="#d9d9d9" />
//         </div>
//         {this.state.localItems.map((name, i) => (
//           <MenuItem tabIndex={i} component="div" ref={this.props.innerRef} disableRipple className={classes.menuItem} style={{ minWidth: (this.props.minWidth) ? this.props.minWidth : 200 }} color={'primary'} key={name} value={name}>
//             <ListItemText primary={name} />
//             <Checkbox checked={this.props.clear ? !this.props.clear : this.state.name.indexOf(name) > -1} />
//           </MenuItem>
//         ))}
//             </Select>
        
//            </div>
//         </FormControl>

//       </div>
//     );
//   }
// }

// MultipleSelects1.propTypes = {
//   classes: PropTypes.object.isRequired,
//   theme: PropTypes.object.isRequired,
// };

// export default withStyles(styles, { withTheme: true })(MultipleSelects1);
















import React, { Component } from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import { withStyles } from '@material-ui/core/styles';
import SVG from 'react-inlinesvg';
import districts_icon from '../../../../images/icon-districts.svg';
import ulbs_icon from '../../../../images/icon-ul-bs.svg';
import styles from './Styles';
import FormControl from '@material-ui/core/FormControl';
import _ from 'lodash'

// const icon = <CheckBoxOutlineBlankIcon fontSize="small" />;
// const checkedIcon = <CheckBoxIcon fontSize="small" />;

class MultipleSelects extends Component {
  constructor(props) {
    super(props)
    this.state = {
      name: this.props.defaultValue || [],
      search: '',
      localItems: this.props.item || [],
    }
  }
  // handleChange = event => {
  //   let { target } = this.props;
  //   let newVals = _.compact(event.target.value);
  //   if (newVals.length > 0) {
  //     this.setState({ name: newVals });
  //     this.props.handleSelected(false, target, newVals)
  //   } else {
  //     this.setState({ name: [] });
  //     this.props.handleSelected(false, target, [])
  //   }
  //   this.props.handleClear()
  // };

  componentWillReceiveProps(nextProps) {
    // if (nextProps.defaultValue !== this.props.defaultValue || !nextProps.defaultValue) {
    //   this.setState({
    //     name: nextProps.defaultValue || []
    //   });
    // }
  }
  getUpdated(event, value) {
    let { target } = this.props;
    let newVals = _.compact(value);
    if (newVals.length > 0) {
      this.setState({ name: newVals }, this.props.handleSelected(false, target, newVals));

    } else {
      this.setState({ name: [] });
      this.props.handleSelected(false, target, [])
    }
    // this.props.handleClear()

  }
  
  render() {
    const { classes, logo, target } = this.props;
    let pls = "All " + target;
    let svgicon;
    if (logo === "DDRs") {
      svgicon = districts_icon;
    } else if (logo === "ULBS") {
      svgicon = ulbs_icon;
    }

    return (
      <div className={classes.root}>
        <FormControl className={classes.formControl} >
          <div className={classes.list}>
            {/* <SVG src={svgicon} className={classes.CloseButton} >
            </SVG> */}
            <Autocomplete
              // fullWidth
              ref={`autocomplete`}
              multiple
              id="checkboxes-tags-demo"
              clearOnEscape
              options={this.state.localItems}
              disableCloseOnSelect
              defaultValue={this.props.clear ? [] : this.state.name}
              onChange={this.getUpdated.bind(this)}
              disableunderline="true"
              filterSelectedOptions={true}
              classes={{ root: classes.select, option: classes.menuItem }}
              getOptionLabel={option => option}
              renderOption={(option, { selected }) => (
                <React.Fragment>
                  {option}
                  {/* <Checkbox
                    icon={icon}
                    checkedIcon={checkedIcon}
                    style={{ right: 2 }}
                    checked={this.state.name.indexOf(option) > -1}
                  /> */}

                </React.Fragment>
              )}
              style={{ minWidth: 134 }}
              // renderTags={params => {
              //   return (
              //     <div>{params && params.length > 0 ? params.length + 'Selected' : 'All' + target}</div>
              //   )
              // }}
              renderInput={params => {
                return (

                  <TextField
                    {...params}
                    // placeholder={pls}
                    inputProps={{
                      // disableUnderline: true,
                      ...params.inputProps,

                      autoComplete: 'disabled', // disable autocomplete and autofill
                      classes: {
                        root: classes.bootstrapRoot,
                        input: classes.bootstrapInput
                      }
                    }}
                    placeholder={this.state.name.length > 0 ? '' : pls}
                    // label="Checkboxes"
                    fullWidth
                  />
                )
              }}
            />
          </div>
        </FormControl>
      </div>
    );
  }
}
export default withStyles(styles, { withTheme: true })(MultipleSelects);



