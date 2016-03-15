package io.mycause.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AddPostToDB {

	@RequestMapping("/postSuccess")
	public ModelAndView addPost(@RequestParam("category") int category, @RequestParam("postheadline") String postheadline,
			@RequestParam("post_desc") String post_desc, HttpServletRequest request) {
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/dbb");
			Connection conn = ds.getConnection();

			// get user_id from cookie
			Cookie[] clientCookies = request.getCookies();
			
			String userID = "";
			int userIDint;
			if (clientCookies[1] != null) {
				userID = clientCookies[1].getValue();
				userIDint = Integer.parseInt(userID);
			} else {
				return new ModelAndView("login", "message", "You must be logged in to create a post.");
			}

			// create mySQL insert statement.
			// -->!! also need to populate the user_id based on who is logged in
			// when post is created.
			String insertStatement = "INSERT INTO maindb.posts(post_headline, post_desc, cat_id, user_id)VALUES(?, ?, ?, ?)";

			// create the mySQL insert preparedstatement
			PreparedStatement insertPreparedStatement = conn.prepareStatement(insertStatement);
			insertPreparedStatement.setString(1, postheadline);
			insertPreparedStatement.setString(2, post_desc);
			insertPreparedStatement.setInt(3, category);
			insertPreparedStatement.setInt(4, userIDint);

			// execute the preparedstatement
			insertPreparedStatement.execute();

			return new ModelAndView ("success", "message", "Your post has been added!");

		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView ("error", "message", "Sorry, something happened. Please go back and try again.");
		}
	}
}