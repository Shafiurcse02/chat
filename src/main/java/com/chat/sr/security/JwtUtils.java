package com.chat.sr.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;


@Component
public class JwtUtils {

	private String SECRET_KEY = "4787dde282ddc43e5889f2fd7e8c1b34bdfb0ef739badc6f6bebc7bc06f1b96218f284694f71dc874f5452c6e5c2f86e8e1bf66a6ea01c4d09d4baf8e00c1654164cc49c936c9ef69ec6b13c80943678db4900504c71553546ee581bfc375f43623e93e2cadfe36b2b5d5033e0a360c1d6c670893c7a44a5c735c2323964b06d8926d31d51f9ec9c1a4cd4b8749802b371c90afd0ea701ffb2bbd706218803ec97efba206c2fc73c3c00a56d5a004be17b7db9fd0e0874a09fb34ec629dfdff87f2bfb246e4c49be5b0f192ad337b0b6b9a6c072bf6d7b6a96bbd4689325299915337003975bf061fb2470a020cf9689cf119a8411c8e9df84d69c7bdd5afd42";

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
	}

	public String generateToken(UserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		return createToken(claims, userDetails.getUsername());

	}

	private String createToken(Map<String, Object> claims, String username) {

		return Jwts.builder().setClaims(claims).setSubject(username).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 6 * 24))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();

	}

	public String extractUsername(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
	}
}
