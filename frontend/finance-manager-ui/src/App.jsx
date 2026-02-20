import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Signup from './pages/Signup';
import Login from './pages/Login';
import OTPVerification from './pages/OTPVerification';
import Dashboard from './pages/Dashboard';
import AddCategory from './pages/AddCategory';
import Categories from './pages/Categories';
import AddTransaction from './pages/AddTransaction';
import Transactions from './pages/Transactions';
import SetBudget from './pages/SetBudget';
import Budgets from './pages/Budgets';
import AddSavingsGoal from './pages/AddSavingsGoal';
import SavingsGoals from './pages/SavingsGoals';
import './App.css';

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />

                <Route path="/login" element={<Login />}/>
                <Route path="/signup" element={<Signup />} />
                <Route path="/verify-otp" element={<OTPVerification />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/add-category" element={<AddCategory />} />
                <Route path="/categories" element={<Categories />} />
                <Route path="/add-transaction" element={<AddTransaction />} />
                <Route path="transactions" element={<Transactions />} />
                <Route path="set-budget" element={<SetBudget />} />
                <Route path="budgets" element={<Budgets />} />
                <Route path="/add-savings-goal" element={<AddSavingsGoal />} />
                <Route path="/savings-goals" element={<SavingsGoals />} />

            </Routes>
        </BrowserRouter>
    );
}

export default App;