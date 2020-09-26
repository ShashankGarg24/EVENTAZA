import React, { Component } from "react";
import classes from "./Login.module.css";
import { withRouter } from "react-router-dom";
import axios from "axios";
class signup extends Component {
  state = {
    name: "",
    username: "",
    password: "",
    wrongpass: false,
  };

  onChangeHandler = (event) => {
    let name = event.target.name;
    let value = event.target.value;
    this.setState({
      [name]: value,
    });
  };

  submit = (e) => {
    e.preventDefault();
    const Data = {
      username: this.state.username,
      password: this.state.password,
    };

    axios
      .post("http://b50cd3051760.ngrok.io/login", Data)
      .then((response) => {
        console.log(response);
        if (response.status === 200) {
          localStorage.setItem("jwt", response.data.jwt);
          console.log(response.data.jwt);
          console.log(localStorage.getItem("jwt"));
          this.toHome();
        }
        this.props.loginname(this.state.username);
      })
      .catch((error) => {
        if (error.response.status === 404) {
          this.setState({ wrongpass: true });
        }
        console.log(error.response.status);
      });
  };
  toHome = () => {
    this.props.history.push("/");
  };
  toRegister = () => {
    this.props.history.push("/Register");
  };

  render() {
    let wrongpassview = "";
    if (this.state.wrongpass === true) {
      wrongpassview = "*wrong credentials*";
    }
    return (
      <div className={classes.Login}>
        <div className={classes.Form}>
          <h4>Login</h4>
          <form onSubmit={this.submit}>
            <div>
              <input
                type="text"
                name="username"
                value={this.state.username}
                placeholder="username"
                onChange={this.onChangeHandler}
              ></input>
            </div>
            <div>
              <input
                type="password"
                name="password"
                value={this.state.password}
                placeholder="password"
                onChange={this.onChangeHandler}
              ></input>
            </div>
            <div className={classes.submit}>
              <input
                style={{ background: "transparent", width: "100%" }}
                type="submit"
              ></input>
            </div>
          </form>
          <p onClick={this.toRegister}>Not yet registered?</p>
          <p style={{ color: "red", fontSize: "12px" }}>{wrongpassview}</p>
        </div>
      </div>
    );
  }
}
export default withRouter(signup);
