import React, { useState, useEffect } from "react";
import { createUser, getUserAccounts, createLinkToken, exchangePublicToken } from "./api";
import { usePlaidLink } from "react-plaid-link";
import './App.css';

function App() {
  const [userId, setUserId] = useState(null);
  const [accounts, setAccounts] = useState([]);
  const [linkToken, setLinkToken] = useState(null);

  useEffect(() => {
    if (userId) {
      fetchAccounts();
    }
  }, [userId]);

  const handleCreateUser = async () => {
    const newUserId = await createUser("Marcos", 25);
    setUserId(newUserId);
    console.log("User created with ID:", newUserId);
  };

  const fetchAccounts = async () => {
    if (!userId) return;
    const userAccounts = await getUserAccounts(userId);
    setAccounts(userAccounts);
  };

  const handleLinkAccount = async () => {
    if (!userId) return;
    const token = await createLinkToken(userId);
    setLinkToken(token);
  };

  const handleSuccess = async (publicToken) => {
    if (!userId) return;
    await exchangePublicToken(userId, publicToken);
    console.log("Successfully linked account!");
    fetchAccounts();
  };

  return (
    <div className="min-h-screen bg-blue-900 flex flex-col items-center p-8">
      <header className="mb-8">
        <h1 className="text-4xl font-bold text-white">Finance Manager</h1>
      </header>
      <main className="w-full max-w-4xl">
        {!userId ? (
          <div className="flex justify-center">
            <button
              className="px-6 py-3 bg-blue-600 text-white font-semibold rounded shadow hover:bg-blue-700 transition duration-200"
              onClick={handleCreateUser}
            >
              Create User
            </button>
          </div>
        ) : (
          <div className="flex flex-col items-center mb-8">
            <p className="text-lg font-medium mb-4">
              User ID: <span className="text-white-600">{userId}</span>
            </p>
            <button
              className="px-6 py-3 bg-green-500 text-white font-semibold rounded shadow hover:bg-green-600 transition duration-200"
              onClick={handleLinkAccount}
            >
              Link Bank Account
            </button>
          </div>
        )}

        {linkToken && (
          <PlaidLinkComponent linkToken={linkToken} onSuccess={handleSuccess} />
        )}

        <section className="mt-12">
          <h2 className="text-2xl text-white font-bold mb-4">Linked Accounts</h2>
          {accounts && accounts.length > 0 ? (
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {accounts.map((account) => (
                <div key={account.account_id} className="bg-white rounded-lg shadow p-6">
                  <h3 className="text-xl font-semibold">{account.name}</h3>
                  <p className="text-gray-600">
                    Balance:{" "}
                    <span className="font-bold">
                      ${account.balances?.current?.toFixed(2)}
                    </span>{" "}
                    {account.balances?.iso_currency_code || "USD"}
                  </p>
                  <p className="text-sm text-gray-500">Type: {account.type}</p>
                  <p className="text-sm text-gray-500">Subtype: {account.subtype}</p>
                </div>
              ))}
            </div>
          ) : (
            <p className="text-center text-gray-500">
              No accounts linked yet. Please link a bank account.
            </p>
          )}
        </section>
      </main>
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
    <div className="flex justify-center mt-6">
      <button
        className="px-6 py-3 bg-purple-600 text-white font-semibold rounded shadow hover:bg-purple-700 transition duration-200"
        onClick={() => open()}
        disabled={!ready}
      >
        Connect Bank Account
      </button>
    </div>
  );
}

export default App;
