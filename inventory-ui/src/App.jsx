import { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { Send, Bot, Database, Plus, X, Save, ArrowUpDown, ArrowUp, ArrowDown } from 'lucide-react';

const API_BASE = 'http://localhost:8080/api';

function App() {
  const [products, setProducts] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [messages, setMessages] = useState([
    { role: 'bot', text: 'Hello! I am your inventory assistant. Ask me anything about your stock.' }
  ]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(false);
  const [showForm, setShowForm] = useState(false);

  // Sorting State
  const [sortConfig, setSortConfig] = useState({ key: 'id', direction: 'asc' });

  // Form State
  const [newProduct, setNewProduct] = useState({ name: '', quantity: '', price: '', supplierId: '' });

  const scrollRef = useRef(null);

  useEffect(() => {
    fetchData();
  }, []);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  const fetchData = async () => {
    try {
      const [prodRes, supRes] = await Promise.all([
        axios.get(`${API_BASE}/products`),
        axios.get(`${API_BASE}/suppliers`)
      ]);
      setProducts(prodRes.data);
      setSuppliers(supRes.data);

      // Auto-select first supplier if available
      if (supRes.data.length > 0) {
        setNewProduct(prev => ({ ...prev, supplierId: supRes.data[0].id }));
      }
    } catch (err) {
      console.error("Failed to fetch data", err);
    }
  };

 const sendMessage = async () => {
     if (!input.trim()) return;
     const userMsg = input;
     setMessages(prev => [...prev, { role: 'user', text: userMsg }]);
     setInput('');
     setLoading(true);

     try {
       const res = await axios.post(`${API_BASE}/query`, { question: userMsg });
       let answer;

       // IMPROVED: Smart Response Parsing
       if (Array.isArray(res.data) && res.data.length > 0) {
         // Case 1: The AI returned a friendly message (e.g. "Hello")
         if (res.data[0].message) {
            answer = res.data[0].message;
         }
         // Case 2: The AI returned an error message
         else if (res.data[0].error) {
            answer = `⚠️ ${res.data[0].error}`;
         }
         // Case 3: Actual Data (e.g. quantity: 60) - Stringify it for now
         else {
            answer = JSON.stringify(res.data);
         }
       } else {
         answer = JSON.stringify(res.data);
       }

       setMessages(prev => [...prev, { role: 'bot', text: answer }]);
       fetchData();
     } catch (err) {
       setMessages(prev => [...prev, { role: 'bot', text: 'Sorry, I encountered an error connecting to the server.' }]);
     } finally {
       setLoading(false);
     }
   };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!newProduct.name || !newProduct.price || !newProduct.supplierId) {
      alert("Please fill all fields and ensure a Supplier is selected.");
      return;
    }
    try {
      await axios.post(`${API_BASE}/products`, newProduct);
      setNewProduct(prev => ({ ...prev, name: '', quantity: '', price: '' })); // Keep supplier selected
      setShowForm(false);
      fetchData();
      setMessages(prev => [...prev, { role: 'bot', text: `✅ Added "${newProduct.name}" to inventory.` }]);
    } catch (err) {
      alert("Failed to create product. Ensure the Supplier ID exists.");
    }
  };

  // --- SORTING LOGIC ---
  const handleSort = (key) => {
    let direction = 'asc';
    if (sortConfig.key === key && sortConfig.direction === 'asc') {
      direction = 'desc';
    }
    setSortConfig({ key, direction });
  };

  const sortedProducts = [...products].sort((a, b) => {
    if (a[sortConfig.key] < b[sortConfig.key]) {
      return sortConfig.direction === 'asc' ? -1 : 1;
    }
    if (a[sortConfig.key] > b[sortConfig.key]) {
      return sortConfig.direction === 'asc' ? 1 : -1;
    }
    return 0;
  });

  const getSortIcon = (columnKey) => {
    if (sortConfig.key !== columnKey) return <ArrowUpDown size={14} className="text-gray-400" />;
    return sortConfig.direction === 'asc' ? <ArrowUp size={14} className="text-blue-500" /> : <ArrowDown size={14} className="text-blue-500" />;
  };

  return (
    <div className="container">
      {/* LEFT: Inventory Dashboard */}
      <div className="card">
        <div className="card-header" style={{ justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Database size={20} className="text-blue-500" />
            <span>Live Inventory</span>
          </div>
          <button
            onClick={() => setShowForm(!showForm)}
            style={{ padding: '0.5rem', background: showForm ? '#ef4444' : '#3b82f6' }}
            title={showForm ? "Cancel" : "Add New Product"}
          >
            {showForm ? <X size={16} /> : <Plus size={16} />}
          </button>
        </div>

        {/* Add Form */}
        {showForm && (
          <div style={{ padding: '1.5rem', background: '#f8fafc', borderBottom: '1px solid #e2e8f0' }}>
            <h4 style={{ margin: '0 0 1rem 0', fontSize: '0.9rem', color: '#64748b' }}>Add New Item</h4>
            {suppliers.length === 0 && (
               <div style={{color: 'red', marginBottom: '10px', fontSize: '0.9rem'}}>Warning: No suppliers found.</div>
            )}
            <form onSubmit={handleCreate} style={{ display: 'flex', flexWrap: 'wrap', gap: '10px', alignItems: 'flex-end' }}>
              <div style={{ flex: '2 1 200px' }}>
                <label style={{fontSize: '0.8rem', color: '#64748b'}}>Name</label>
                <input style={{width: '100%', padding: '8px'}} placeholder="Product Name" value={newProduct.name} onChange={e => setNewProduct({...newProduct, name: e.target.value})} required />
              </div>
              <div style={{ flex: '1 1 80px' }}>
                <label style={{fontSize: '0.8rem', color: '#64748b'}}>Qty</label>
                <input type="number" style={{width: '100%', padding: '8px'}} value={newProduct.quantity} onChange={e => setNewProduct({...newProduct, quantity: e.target.value})} required />
              </div>
              <div style={{ flex: '1 1 100px' }}>
                <label style={{fontSize: '0.8rem', color: '#64748b'}}>Price</label>
                <input type="number" style={{width: '100%', padding: '8px'}} value={newProduct.price} onChange={e => setNewProduct({...newProduct, price: e.target.value})} required />
              </div>
              <div style={{ flex: '1 1 150px' }}>
                 <label style={{fontSize: '0.8rem', color: '#64748b'}}>Supplier</label>
                 <select style={{width: '100%', padding: '9px', borderRadius: '8px', border: '1px solid #e2e8f0'}} value={newProduct.supplierId} onChange={e => setNewProduct({...newProduct, supplierId: e.target.value})} required>
                   <option value="" disabled>Select...</option>
                   {suppliers.map(s => (<option key={s.id} value={s.id}>{s.name} (ID: {s.id})</option>))}
                 </select>
              </div>
              <button type="submit" style={{ display: 'flex', alignItems: 'center', gap: '5px', height: '38px', marginTop: 'auto' }}>
                <Save size={16} /> Save
              </button>
            </form>
          </div>
        )}

        <div style={{ overflow: 'auto', flex: 1 }}>
          <table>
            <thead>
              <tr>
                <th onClick={() => handleSort('id')} style={{cursor: 'pointer'}}>
                  <div style={{display:'flex', alignItems:'center', gap:'5px'}}>ID {getSortIcon('id')}</div>
                </th>
                <th onClick={() => handleSort('name')} style={{cursor: 'pointer'}}>
                   <div style={{display:'flex', alignItems:'center', gap:'5px'}}>Product Name {getSortIcon('name')}</div>
                </th>
                <th onClick={() => handleSort('quantity')} style={{cursor: 'pointer'}}>
                   <div style={{display:'flex', alignItems:'center', gap:'5px'}}>Quantity {getSortIcon('quantity')}</div>
                </th>
                <th onClick={() => handleSort('price')} style={{cursor: 'pointer'}}>
                   <div style={{display:'flex', alignItems:'center', gap:'5px'}}>Price {getSortIcon('price')}</div>
                </th>
              </tr>
            </thead>
            <tbody>
              {sortedProducts.length === 0 ? (
                <tr><td colSpan="4" style={{textAlign:'center', color: '#999', padding: '2rem'}}>No products found</td></tr>
              ) : (
                sortedProducts.map(p => (
                  <tr key={p.id}>
                    <td>#{p.id}</td>
                    <td style={{fontWeight: 500}}>{p.name}</td>
                    <td>
                      <span style={{
                        padding: '4px 8px',
                        borderRadius: '12px',
                        background: p.quantity < 10 ? '#fee2e2' : '#dcfce7',
                        color: p.quantity < 10 ? '#991b1b' : '#166534',
                        fontSize: '0.85em',
                        fontWeight: 'bold'
                      }}>
                        {p.quantity} units
                      </span>
                    </td>
                    <td>${p.price}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* RIGHT: AI Chat */}
      <div className="card">
        <div className="card-header" style={{ justifyContent: 'space-between' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Bot size={24} color="#3b82f6" />
            <span>AI Assistant</span>
          </div>
          {/* Clear Chat Button */}
          {messages.length > 1 && (
            <button
              onClick={() => setMessages([{ role: 'bot', text: 'Hello! I am your inventory assistant. Ask me anything about your stock.' }])}
              style={{background: 'transparent', color: '#64748b', padding: '4px', height: 'auto', width: 'auto'}}
              title="Clear Chat"
            >
              <X size={16} />
            </button>
          )}
        </div>

        <div className="chat-messages" ref={scrollRef}>
          {messages.map((msg, idx) => (
            <div key={idx} className={`message ${msg.role}`}>{msg.text}</div>
          ))}

          {/* Typing Indicator */}
          {loading && (
            <div className="message bot">
              <div className="typing-indicator">
                <div className="typing-dot"></div>
                <div className="typing-dot"></div>
                <div className="typing-dot"></div>
              </div>
            </div>
          )}
        </div>

        <div className="chat-input-area">
          <input
            type="text"
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyPress={e => e.key === 'Enter' && sendMessage()}
            placeholder="Ask: 'Which items are low on stock?'"
            disabled={loading}
          />
          <button onClick={sendMessage} disabled={loading}><Send size={18} /></button>
        </div>
      </div>
    </div>
  );
}

export default App;