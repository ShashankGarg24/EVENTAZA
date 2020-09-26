import React, { Component } from 'react';
import classes from './Layout.module.css';
import Header from '../Components/Header/Header'
import { Route, Switch,withRouter} from 'react-router-dom';
import Login from '../Containers/Login/Login'
import Signup from '../Containers/Signup/Signup'
import Home from '../Containers/LandingPage/LandingPage'
import Footer from '../Components/Footer/Footer'
// import Toolbar from '../../components/Navigation/Toolbar/Toolbar';
// import SideDrawer from '../../components/Navigation/SideDrawer/SideDrawer';

class Layout extends Component {
    state={
            name:'',
            showSideDrawer: false,
            loginstatus:false
          };
      loginnameHandler = (newname)=>{
        this.setState({
            name:newname,
            loginstatus:true
        })
        console.log(this.state.name);
      }
    
    sideDrawerClosedHandler = () => {
        this.setState( { showSideDrawer: false } );
    }

    sideDrawerToggleHandler = () => {
        this.setState( ( prevState ) => {
            return { showSideDrawer: !prevState.showSideDrawer };
        } );
    }
    toggle =()=>{
        this.props.history.push('/login');
    }
    render () {
        let token = localStorage.getItem('jwt');
        return (
           <div className={classes.Layout}>
               <div style={{position:'sticky',top:0,zIndex:5}}>
               <Header name={this.state.name}></Header>
               </div>
               {/* <Signup loginname={this.loginnameHandler}/> */}
               {this.state.name};
               <Switch>
              <Route path='/' exact render={() => <Home></Home>}></Route>
                <Route path='/register'exact render={() => (<Signup />)} ></Route>
                { token ===null ?
                    <Route path='/login' exact render ={ () => (<Login loginname={this.loginnameHandler} ></Login>)}></Route>:<Route path='/login' exact render ={ () => (<Home></Home>)}></Route>}
                
                </Switch>
                <Footer />
           </div>
        )
    }
}

export default withRouter(Layout);