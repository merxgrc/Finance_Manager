import axios from "axios";

const API_URL = "http://localhost:8080";  

export const createUser = async (name, age) => {
  const response = await axios.post(`${API_URL}/users`, { name, age });
  return response.data;  // Returns the new user's ID
};

export const getUserAccounts = async (userId) => {
  try {
    console.log(`Fetching accounts for user ${userId}...`);
    const response = await axios.post(`http://localhost:8080/plaid/get_accounts`, { userId });
    console.log("API Response:", response.data);
    return response.data;
  } catch (error) {
    console.error("Error fetching accounts:", error.response?.data || error.message);
    throw error;
  }
};

export const createLinkToken = async (userId) => {
  const response = await axios.post(`${API_URL}/plaid/create_link_token`, { userId });
  return response.data.link_token;
};

export const exchangePublicToken = async (userId, publicToken) => {
  const response = await axios.post(`${API_URL}/plaid/exchange_public_token`, { userId, publicToken });
  return response.data;
};
