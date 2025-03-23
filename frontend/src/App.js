import React, { useState, useEffect } from "react";
import { createUser, getUserAccounts, createLinkToken, exchangePublicToken } from "./api";
import { usePlaidLink } from "react-plaid-link";

function App() {
  const [userId, setUserId] = useState(null);
  const [accounts, setAccounts] = useState([]);
  const [linkToken, setLinkToken] = useState(null);
  const [publicToken, setPublicToken] = useState(null);

  useEffect(() => {
    if (userId) {
      fetchAccounts();
    }
  }, [userId]);

  const handleCreateUser = async () => {
    const newUserId = await createUser("Marcos", 25);
    setUserId(newUserId);
    console.log("User created with ID:", newUserId);
  
    // After user creation, trigger linking a bank account
    const linkToken = await createLinkToken(newUserId);
    setLinkToken(linkToken);
  };
  

  const fetchAccounts = async () => {
    if (!userId) return;
    const userAccounts = await getUserAccounts(userId);
    setAccounts(userAccounts);
  };

  const handleLinkAccount = async () => {
    if (!userId) return;
    const linkToken = await createLinkToken(userId);
    setLinkToken(linkToken);
  };

  const handleSuccess = async (publicToken) => {
    if (!userId) return;
  
    await exchangePublicToken(userId, publicToken);
    console.log("Successfully linked account!");
  
    // Fetch accounts AFTER linking
    fetchAccounts();
  };
  

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-100 p-5">
      <h1 className="text-3xl font-bold mb-4">Finance Manager</h1>

      {!userId ? (
        <button
          className="px-4 py-2 bg-blue-600 text-white rounded"
          onClick={handleCreateUser}
        >
          Create User
        </button>
      ) : (
        <>
          <p>User ID: {userId}</p>
          <button
            className="px-4 py-2 bg-green-500 text-white rounded mt-3"
            onClick={handleLinkAccount}
          >
            Link Bank Account
          </button>
        </>
      )}

      {linkToken && <PlaidLinkComponent linkToken={linkToken} onSuccess={handleSuccess} />}

      <div className="mt-5 w-full max-w-md">
        <h2 className="text-xl font-semibold">Linked Accounts</h2>
        <ul className="mt-2">
        {accounts.map((account) => (
  <div key={account.account_id}>
    <h3>{account.name}</h3>
    <p>Balance: ${account.balances?.current?.toFixed(2)} {account.balances?.iso_currency_code || "USD"}</p>
  </div>
))}
        </ul>
      </div>
    </div>
  );
}

function PlaidLinkComponent({ linkToken, onSuccess }) {
  const config = {
    token: linkToken,
    onSuccess: (publicToken) => {
      console.log("Public Token Received:", publicToken);
      onSuccess(publicToken);
    },
  };

  const { open, ready } = usePlaidLink(config);

  return (
    <button
      className="px-4 py-2 bg-purple-600 text-white rounded mt-3"
      onClick={() => open()}
      disabled={!ready}
    >
      Connect Bank Account
    </button>
  );
}

export default App;

