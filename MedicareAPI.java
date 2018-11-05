import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.DefaultComboBoxModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MedicareAPI {

	private JFrame frmSupplierDirectoryData;
	public JSONArray jsnArr;
	public Vector<String> columns = new Vector<>();
	public Vector<String> rows = new Vector<>();
	private JTextField txtLimit;
	private JTextField keyWord;
	private JTable table;
	private JComboBox fieldName;
	public DefaultTableModel model = new DefaultTableModel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MedicareAPI window = new MedicareAPI();
					window.frmSupplierDirectoryData.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MedicareAPI() {
		initialize();
	}


	public JSONArray getJsnArr() {
		return jsnArr;
	}

	public void setJsnArr(JSONArray jsnArr) {
		this.jsnArr = jsnArr;
	}
	
	public JSONObject makeHttpRequest(String url,String method,List<NameValuePair>params) {
		InputStream is = null;
		String json = "";
		JSONObject jObj = null;
		
		try {
			if(method == "POST") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				httpPost.setEntity(new UrlEncodedFormEntity(params));
				
				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}else if(method == "GET") {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				String paramString = URLEncodedUtils.format(params, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				
				HttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity httpEntity = httpResponse.getEntity();
				is = httpEntity.getContent();
			}
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"),8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			
			while ((line = reader.readLine())!=null) {
				sb.append(line +  "\n");
			}
			is.close();
			json = sb.toString();
			jObj = new JSONObject(json);
		}catch (JSONException e) {
			try {
				JSONArray jsnArr = new JSONArray(json);
				setJsnArr(jsnArr);
			}catch(JSONException ee) {
				ee.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return jObj;
		
	}
	
	public DefaultTableModel viewTable() throws Exception
	{	
		while (model.getRowCount()>0) {
			model.removeRow(0);
		}
		jsnArr=getJsnArr();
		int i = 0;
		
		for(i=0;i<jsnArr.length();i++)
		{
			rows = new Vector<>();
			JSONObject result = jsnArr.getJSONObject(i);
			rows.addElement(result.optString("cbsa_id"));
			rows.addElement(result.optString("cbsa_name"));
			rows.addElement(result.optString("company_name"));
			rows.addElement(result.optString("address"));
			rows.addElement(result.optString("address_2"));
			rows.addElement(result.optString("city"));
			rows.addElement(result.optString("state"));
			rows.addElement(result.optString("zip"));
			rows.addElement(result.optString("phone"));
			rows.addElement(result.optString("toll_free_phone"));
			rows.addElement(result.optString("prod_ctgry_name"));
			rows.addElement(result.optString("competitive_bid"));
			System.out.println(rows);
			model.addRow(rows);
		}

		return model;
	}
	
	public void setColumnIdentifier()
	{

		columns.add("Compettitive Bid Service Area ID");
		columns.add("Compettitive Bid Service Area Name");
		columns.add("Company Name");
		columns.add("Address");
		columns.add("Address2");
		columns.add("City");
		columns.add("State");
		columns.add("Zip");
		columns.add("Phone");
		columns.add("Toll-Free-Telephone");
		columns.add("Product Category Name");
		columns.add("Competitve Bid");
		
		model.setColumnIdentifiers(columns);
	}
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSupplierDirectoryData = new JFrame();
		frmSupplierDirectoryData.setTitle("Supplier Directory Data");
		frmSupplierDirectoryData.setBackground(new Color(255, 250, 250));
		frmSupplierDirectoryData.getContentPane().setBackground(new Color(255, 250, 250));
		frmSupplierDirectoryData.setBounds(100, 100, 1240, 800);
		frmSupplierDirectoryData.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSupplierDirectoryData.getContentPane().setLayout(null);
		
		setColumnIdentifier();
		JButton btnSearch = new JButton("SHOW RESULTS");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				Thread thread = new Thread(new Runnable()
				{
					public void run()
					{
						
						if(txtLimit.getText().isEmpty())
						{
							JOptionPane.showMessageDialog(null, "Please fill in the number of result you wish to show!");
						}
						else if(keyWord.getText().isEmpty())
						{
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("$$app_token","twEeTRe4sB31Vyr9kptyvzFds"));
							params.add(new BasicNameValuePair("$limit",txtLimit.getText()));
						
							String strUrl = "https://data.medicare.gov/resource/jdzs-juf6.json";
							makeHttpRequest(strUrl,"GET",params);
							jsnArr = getJsnArr();
							
							try 
							{
								if(jsnArr.length()==0) 
								{
									JOptionPane.showMessageDialog(null, "No Result Found");
								}
								else
								{
									model = viewTable();
									table.setModel(model);
								}
								
							}catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						else 
						{
							String field = String.valueOf(fieldName.getSelectedItem());
							List<NameValuePair> params = new ArrayList<NameValuePair>();
							params.add(new BasicNameValuePair("$$app_token","twEeTRe4sB31Vyr9kptyvzFds"));
							params.add(new BasicNameValuePair("$limit",txtLimit.getText()));		
							params.add(new BasicNameValuePair(field,keyWord.getText()));
						
							String strUrl = "https://data.medicare.gov/resource/jdzs-juf6.json";
							makeHttpRequest(strUrl,"GET",params);
							jsnArr = getJsnArr();
							
							
							try 
							{
								if(jsnArr.length()==0) 
								{
									System.out.println(jsnArr.length());
									JOptionPane.showMessageDialog(null, "No Result Found");
									
								}
								else
								{
									model = viewTable();
									table.setModel(model);
								}
								
							}catch(Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				});
				thread.start();
			}
		});
		
		JLabel lblPleaseKeyIn = new JLabel("Please key in the number of results you wanted to show (MANDATORY):");
		lblPleaseKeyIn.setBounds(21, 37, 505, 22);
		frmSupplierDirectoryData.getContentPane().add(lblPleaseKeyIn);
		btnSearch.setBounds(735, 35, 175, 29);
		frmSupplierDirectoryData.getContentPane().add(btnSearch);
		
		txtLimit = new JTextField();
		txtLimit.setBounds(533, 35, 175, 27);
		frmSupplierDirectoryData.getContentPane().add(txtLimit);
		txtLimit.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBackground(new Color(245, 255, 250));
		panel.setBounds(21, 92, 687, 146);
		frmSupplierDirectoryData.getContentPane().add(panel);
		panel.setLayout(null);
		
		fieldName = new JComboBox();
		fieldName.setModel(new DefaultComboBoxModel(new String[] {"cbsa_id", "cbsa_name", "company_name", "city", "state", "zip", "prod_ctgry_name", "competitve_bid"}));
		fieldName.setBounds(139, 57, 276, 27);
		
		panel.add(fieldName);
		
		JLabel lblSelectTheCategory = new JLabel("Select the category and key in the keyword you wish to search (OPTIONAL):");
		lblSelectTheCategory.setBounds(16, 17, 515, 28);
		panel.add(lblSelectTheCategory);
		
		JLabel lblCategory = new JLabel("Category:");
		lblCategory.setBounds(16, 61, 113, 16);
		panel.add(lblCategory);
		
		JLabel lblKeyword = new JLabel("KeyWord:");
		lblKeyword.setBounds(16, 101, 61, 16);
		panel.add(lblKeyword);
		
		keyWord = new JTextField();
		keyWord.setBounds(139, 96, 276, 26);
		panel.add(keyWord);
		keyWord.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(21, 284, 1200, 325);
		frmSupplierDirectoryData.getContentPane().add(scrollPane);
		
		table = new JTable();
		table.setAutoCreateRowSorter(true);
		table.setBackground(Color.WHITE);
		scrollPane.setViewportView(table);
		
	}
}
