package org.jthreadutils.distribution;

/**
 * This interface give possibility for developers to provide common
 * identification key for subset of data (group id), which will be handle by dedicated thread.
 * 
 * It is developer responsible to chose the best portion of data in order to guarantee that 
 * subset of data will be indicated as sequence of data in relation.
 * In example from table below the best will be to chose combination of <code>SHOP</code>, <code>CUSTOMER</code>,
 * <CODE>ORDER</CODE>, <CODE>ORDER_ITEM</code>. 
 * <br/>
 * <br/>
 *  
 * <table border="1">
 * 	<tr>
 * 		<td>EVENT_INDEX</td>
 * 		<td>SHOP</td>
 *      <td>CUSTOMER</td>
 * 		<td>ORDER</td>
 * 		<td>ORDER_ITEM</td>
 * 		<td>COUNT</td>
 *  </tr>
 *  <tbody>
 *  	<tr>
 *  		<td>1</td>
 *  		<td>Foobar.org</td>
 *  		<td>Customer_11</td>
 *  		<td>1</td>
 *  		<td>Hat</td>
 *  		<td>1</td>
 *  	</tr>
 *  	<tr>
 *  		<td>2</td>
 *  		<td>Foobar.org</td>
 *  		<td>Customer_11</td>
 *  		<td>1</td>
 *  		<td>Gloves</td>
 *  		<td>2</td>
 *  	</tr>
 *  	<tr>
 *  		<td>3</td>
 *  		<td>Foobar.org</td>
 *  		<td>Customer_11</td>
 *  		<td>1</td>
 *  		<td>Hat</td>
 *  		<td>2</td>
 *  	</tr>
 *  </tbody>
 * </table>
 * 
 * @author adam-wypych
 * @since 1.0.0
 * @version %I%, %G%
 */
public interface DataGroupIdExtractor<I, O> {
	
	/**
	 * This method provides functionality to extracts from given element of data
	 * unique key in order to segregate data which belongs to same group of data. 
	 * 
	 * @param input is single data element 
	 * @return unique queue of subset of data
	 * 
	 * @author adam-wypych
	 * @since 1.0.0
	 * @version %I%, %G%
	 */
	O extractGroupId(final I data);
}
